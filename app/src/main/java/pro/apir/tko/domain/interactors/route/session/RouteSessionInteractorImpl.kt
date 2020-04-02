package pro.apir.tko.domain.interactors.route.session

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.map
import pro.apir.tko.core.functional.onRight
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.failure.RouteTrackingFailure
import pro.apir.tko.domain.failure.TrackingFailureCode
import pro.apir.tko.domain.model.*
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel
import java.io.File
import javax.inject.Inject

class RouteSessionInteractorImpl @Inject constructor(private val sessionRepository: RouteSessionRepository,
                                                     private val photoRepository: RoutePhotoRepository,
                                                     private val attachmentRepository: AttachmentRepository,
                                                     private val userRepository: UserRepository) : RouteSessionInteractor {

    /**
     *
     * Return ID of Route of existing RouteSession
     *
     */
    override suspend fun getCurrentTrackingInfo(): Either<Failure, RouteTrackingInfoModel?> = withContext(Dispatchers.IO) {
        sessionRepository.getCurrentRouteTrackingInfo()
    }


    /**
     *
     * Create initial RouteSessionModel from RouteModel
     *
     */
    override suspend fun getInitialSessionFromRoute(routeModel: RouteModel): Either<Failure, RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            val userIdResult = userRepository.getUserId()
            //get current session
            val existingTrackingInfo = getCurrentTrackingInfo()

            //
            when (existingTrackingInfo) {
                is Either.Left -> existingTrackingInfo
                is Either.Right -> {
                    when (true) {
                        existingTrackingInfo.b != null && existingTrackingInfo.b.routeId == routeModel.id.toLong() -> {
                            val session = mapSessionWithInfo(RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_IN_PROGRESS), existingTrackingInfo.b)
                            Either.Right(session)
                        }
                        existingTrackingInfo.b == null -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_DEFAULT)
                            Either.Right(route)
                        }
                        else -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_START_DISABLED)
                            Either.Right(route)
                        }
                    }
                }
            }


        }
    }

    /**
     *
     *  Start RouteSessionModel or Resume it with saved progress
     *
     */
    override suspend fun startSession(routeSessionModel: RouteSessionModel): Either<Failure, RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            val userIdResult = userRepository.getUserId()
            //CHECK EXISTING SESSION FOR THIS ROUTE AND USER
            val existingSession = sessionRepository.getCurrentRouteTrackingInfo()
            if (existingSession is Either.Right && existingSession.b != null) {
                val session = mapSessionWithInfo(routeSessionModel, existingSession.b).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                Either.Right(session)
            } else {
                //NOT EXISTS -> START ROUTE TRACKING
                val startResult = sessionRepository.startRouteTracking(routeSessionModel.routeId.toLong())
                when (startResult) {
                    is Either.Left -> Either.Left(startResult.a)
                    is Either.Right -> {
                        val session = mapSessionWithInfo(routeSessionModel, startResult.b).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                        Either.Right(session)
                    }
                }
            }

        }

    }


    /**
     * Find and map existing route session to route list model
     */
    override suspend fun mapRouteListWithExisting(list: List<RouteModel>): Either<Failure, List<RouteModel>> {
        return withContext(Dispatchers.IO) {
            val routeId = getCurrentTrackingInfo()
            routeId.map { tracking ->
                list.map { model -> RouteModel(model, model.id.toLong() == tracking?.routeId) }
            }
        }
    }

    /**
     * Completes given route point model and return list with new states
     */
    override suspend fun completePoint(routeSessionModel: RouteSessionModel, routePointId: Long): Either<Failure, RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            val enterResult = sessionRepository.enterRouteStop(routePointId)

            when (enterResult) {
                is Either.Left -> {
                    when (true) {

                        enterResult.a is RouteTrackingFailure
                                && enterResult.a.code == TrackingFailureCode.ALREADY_ENTERED -> completePointRequest(routeSessionModel, routePointId, this)

                        else -> Either.Left(enterResult.a)

                    }
                }
                is Either.Right -> {
                    completePointRequest(routeSessionModel, routePointId, this)
                }
            }

            return@withContext updateSession(routeSessionModel)
        }
    }

    private suspend fun completePointRequest(routeSessionModel: RouteSessionModel, routePointId: Long, scope: CoroutineScope) {
        val photos = arrayListOf<String>()
        if (routeSessionModel.sessionId != null) {
            val photosToUpload = photoRepository.getPhotosByPoint(routeSessionModel.sessionId, routePointId)
            photosToUpload.forEach { cachePhoto ->
                try {
                    attachmentRepository.uploadFile(File(cachePhoto.path)).onRight { uploadedPhotos ->
                        photos.addAll(uploadedPhotos.map { it.id.toString() })
                        scope.launch {
                            photoRepository.deletePhoto(cachePhoto.id)
                        }

                    }
                } catch (e: Exception) {
                }

            }
        }
        val completeResult = sessionRepository.leaveRouteStop(photos)
        when (completeResult) {
            is Either.Left -> Either.Left(completeResult.a)
        }
    }

    private suspend fun updateSession(sessionModel: RouteSessionModel): Either<Failure, RouteSessionModel> {
        val info = getCurrentTrackingInfo()
        return when (info) {
            is Either.Left -> Either.Left(info.a)
            is Either.Right -> {
                return if (info.b != null) Either.Right(mapSessionWithInfo(sessionModel, info.b)) else Either.Right(sessionModel)
            }
        }
    }


    private fun mapSessionWithInfo(sessionModel: RouteSessionModel, infoModel: RouteTrackingInfoModel): RouteSessionModel {

        var completedCount = 0
        var completedLastPos = -1
        val sessionId = infoModel.sessionId
        val mappedPoints = arrayListOf<RoutePointModel>()
        sessionModel.points.forEachIndexed { index, point ->

            val infoPoint = infoModel.stopsCompleted.findLast { info ->
                Log.e("httpStop", "info routeStopId: ${info.routeStopId} point pointId: ${point.pointId}")
                info.routeStopId == point.pointId
            }


            val newPoint = if (infoPoint != null) {
                completedCount++
                completedLastPos = index
                val photos = infoPoint.attachments.map { PhotoModel(it.id.toLong(), PhotoModel.Type.REMOTE, it.url) }
                RoutePointModel(RouteStateConstants.POINT_TYPE_COMPLETED, photos, point)
            } else {
                val photos =
                        infoModel.localPhotos
                                .filter { cache -> cache.pointId == point.pointId.toLong() }
                                .map { PhotoModel(it.id, PhotoModel.Type.LOCAL, it.path) }
                RoutePointModel(RouteStateConstants.POINT_TYPE_DEFAULT, photos, point)
            }
            mappedPoints.add(newPoint)
        }

        val isCompleted = completedCount == mappedPoints.size && completedLastPos == mappedPoints.size - 1

        if (!isCompleted)
            setPendingPoint(mappedPoints)

        val newSession = RouteSessionModel(sessionId, mappedPoints, sessionModel)
                .apply { state = if (isCompleted) RouteStateConstants.ROUTE_TYPE_COMPLETED else RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }

        return newSession
    }

    /**
     *  Find next to last completed RoutePointModel and set it type to pending
     */
    private fun setPendingPoint(points: List<RoutePointModel>) {
        var lastCompleted = -1

        points.forEachIndexed { index, routePointModel ->
            if (routePointModel.type == RouteStateConstants.POINT_TYPE_COMPLETED) lastCompleted = index
        }

        if (lastCompleted + 1 < points.size) {
            points[lastCompleted + 1].type = RouteStateConstants.POINT_TYPE_PENDING
        }

    }

    private fun checkCompletion(routeSessionModel: RouteSessionModel): Boolean {
        var flag = true
        routeSessionModel.points.forEach {
            if (it.type != RouteStateConstants.POINT_TYPE_COMPLETED)
                flag = false
        }
        return flag
    }

}