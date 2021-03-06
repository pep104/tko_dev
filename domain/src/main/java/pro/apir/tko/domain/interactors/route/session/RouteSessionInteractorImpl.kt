package pro.apir.tko.domain.interactors.route.session

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.map
import pro.apir.tko.core.data.onSuccess
import pro.apir.tko.domain.failure.RouteTrackingFailure
import pro.apir.tko.domain.failure.RouteTrackingNotExist
import pro.apir.tko.domain.failure.TrackingFailureCode
import pro.apir.tko.domain.model.*
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel
import pro.apir.tko.domain.repository.attachment.AttachmentRepository
import pro.apir.tko.domain.repository.host.HostRepository
import pro.apir.tko.domain.repository.route.RouteSessionRepository
import pro.apir.tko.domain.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.repository.user.UserRepository
import java.io.File
import javax.inject.Inject

class RouteSessionInteractorImpl @Inject constructor(private val sessionRepository: RouteSessionRepository,
                                                     private val photoRepository: RoutePhotoRepository,
                                                     private val attachmentRepository: AttachmentRepository,
                                                     private val userRepository: UserRepository,
                                                     private val hostRepository: HostRepository,
) : RouteSessionInteractor {

    /**
     *
     * Return ID of Route of existing RouteSession
     *
     */
    override suspend fun getCurrentTrackingInfo(): Resource<RouteTrackingInfoModel?> = withContext(Dispatchers.IO) {
        sessionRepository.getCurrentRouteTrackingInfo()
    }

    /**
     *
     * FLOW
     * 1) Create initial RouteSessionModel from RouteModel
     * 2) Create actual session with tracking api
     *
     */
    override suspend fun getSessionFromRoute(routeModel: RouteModel): Flow<Resource<RouteSessionModel>> = flow {
        withContext(Dispatchers.IO) {
            //Create and emit initial session
            val initialSession = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_START_DISABLED)
            emit(Resource.Success(initialSession))

            //Request for actual session
            val actualSession = getActualSession(routeModel)
            emit(actualSession)
        }
    }

    /**
     * Creates session with tracking info
     */
    suspend fun getActualSession(routeModel: RouteModel): Resource<RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            //get current session
            val existingTrackingInfo = getCurrentTrackingInfo()

            //
            when (existingTrackingInfo) {
                is Resource.Error -> existingTrackingInfo
                is Resource.Success -> {
                    when (true) {
                        existingTrackingInfo.data != null && existingTrackingInfo.data!!.routeId == routeModel.id.toLong() -> {
                            val session = mapSessionWithInfo(RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_IN_PROGRESS), existingTrackingInfo.data!!)
                            Resource.Success(session)
                        }
                        existingTrackingInfo.data == null -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_DEFAULT)
                            Resource.Success(route)
                        }
                        else -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_START_DISABLED)
                            Resource.Success(route)
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
    override suspend fun startSession(routeSessionModel: RouteSessionModel): Resource<RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            val userIdResult = userRepository.getUserId()
            //CHECK EXISTING SESSION FOR THIS ROUTE AND USER
            val existingSession = sessionRepository.getCurrentRouteTrackingInfo()
            if (existingSession is Resource.Success && existingSession.data != null) {
                val session = mapSessionWithInfo(routeSessionModel, existingSession.data!!).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                Resource.Success(session)
            } else {
                //NOT EXISTS -> START ROUTE TRACKING
                val startResult = sessionRepository.startRouteTracking(routeSessionModel.routeId.toLong())
                when (startResult) {
                    is Resource.Error -> Resource.Error(startResult.failure)
                    is Resource.Success -> {
                        val session = mapSessionWithInfo(routeSessionModel, startResult.data).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                        Resource.Success(session)
                    }
                }
            }

        }

    }

    /**
     * Finish route tracking for completed session
     */
    override suspend fun finishSession(routeSessionModel: RouteSessionModel): Resource<RouteSessionModel> = withContext(Dispatchers.IO) {
        return@withContext if (routeSessionModel.sessionId != null) {
            val result = sessionRepository.finishRouteTracking(routeSessionModel.sessionId)
            when (result) {
                is Resource.Error -> Resource.Error(result.failure)
                is Resource.Success -> Resource.Success(mapSessionWithInfo(routeSessionModel, result.data))
            }
        } else
            Resource.Error(RouteTrackingNotExist())
    }

    /**
     * Find and map existing route session to route list model
     */
    override suspend fun mapRouteListWithExisting(list: List<RouteModel>): Resource<List<RouteModel>> {
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
    override suspend fun completePoint(routeSessionModel: RouteSessionModel, routePointId: Long): Resource<RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            val enterResult = sessionRepository.enterRouteStop(routePointId)

            when (enterResult) {
                is Resource.Error -> {
                    when (true) {

                        enterResult.failure is RouteTrackingFailure
                                && (enterResult.failure as RouteTrackingFailure).code == TrackingFailureCode.ALREADY_ENTERED -> completePointRequest(routeSessionModel, routePointId, this)

                        else -> Resource.Error(enterResult.failure)

                    }
                }
                is Resource.Success -> {
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
                    attachmentRepository.uploadFile(File(cachePhoto.path)).onSuccess { uploadedPhotos ->
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
            is Resource.Error -> Resource.Error(completeResult.failure)
        }
    }

    private suspend fun updateSession(sessionModel: RouteSessionModel): Resource<RouteSessionModel> {
        val info = getCurrentTrackingInfo()
        return when (info) {
            is Resource.Error -> Resource.Error(info.failure)
            is Resource.Success -> {
                return if (info.data != null) Resource.Success(mapSessionWithInfo(sessionModel, info.data!!)) else Resource.Success(sessionModel)
            }
        }
    }


    private fun mapSessionWithInfo(sessionModel: RouteSessionModel, infoModel: RouteTrackingInfoModel): RouteSessionModel {
        val hostUrl = hostRepository.getHost().toUrl()
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
                val photos = infoPoint.attachments.map { PhotoModel(it.id.toLong(), PhotoModel.Type.REMOTE, "${hostUrl}${it.url}") }
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