package pro.apir.tko.domain.interactors.route.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.map
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel
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
    override suspend fun getCurrentTrackingSession(): Either<Failure, RouteTrackingInfoModel?> = withContext(Dispatchers.IO) {
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
            val existingSession = sessionRepository.getCurrentRouteTrackingInfo()

            //
            when (existingSession) {
                is Either.Left -> existingSession
                is Either.Right -> {
                    when (true) {
                        existingSession.b?.routeId == routeModel.id.toLong() -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_IN_PROGRESS)
                            val session = sessionRepository.resumeSession(userIdResult, route).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                            setPendingPoint(session.points)
                            Either.Right(session)
                        }
                        existingSession.b == null -> {
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
                val session = sessionRepository.resumeSession(userIdResult, routeSessionModel).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                setPendingPoint(session.points)
                Either.Right(session)
            } else {
                val session = sessionRepository.createSession(userIdResult, routeSessionModel).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                setPendingPoint(session.points)
                Either.Right(session)
            }

        }

    }


    /**
     * Find and map existing route session to route list model
     */
    override suspend fun mapRouteListWithExisting(list: List<RouteModel>): Either<Failure, List<RouteModel>> {
        return withContext(Dispatchers.IO) {
            val routeId = getCurrentTrackingSession()
            routeId.map { tracking ->
                list.map { model -> RouteModel(model, model.id.toLong() == tracking?.routeId) }
            }
        }
    }

    /**
     *
     */
    override suspend fun updateSession(routeSessionModel: RouteSessionModel): RouteSessionModel {
        val result = sessionRepository.updateSession(routeSessionModel)
        if (result.state == RouteStateConstants.ROUTE_TYPE_IN_PROGRESS)
            setPendingPoint(result.points)
        return result
    }

    override suspend fun startPoint(routeSessionModel: RouteSessionModel, routePointId: Long): Either<Failure, RouteSessionModel> {
        TODO()
    }


    /**
     * Completes given route point model and return list with new states
     */
    override suspend fun completePoint(routeSessionModel: RouteSessionModel, routePointId: Long): Either<Failure, RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            //TODO LOAD PHOTOS TO SERVER?
            val photos = mutableListOf<String>()

            val newType = RouteStateConstants.POINT_TYPE_COMPLETED
            sessionRepository.updatePoint(routePointId, photos, newType)
            routeSessionModel.points.find { it.id == routePointId }?.type = newType
            val isCompleted = checkCompletion(routeSessionModel)
            if (isCompleted) {
                routeSessionModel.state = RouteStateConstants.ROUTE_TYPE_COMPLETED
                sessionRepository.finishSession(routeSessionModel)
            }


            Either.Right(updateSession(routeSessionModel))
        }
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