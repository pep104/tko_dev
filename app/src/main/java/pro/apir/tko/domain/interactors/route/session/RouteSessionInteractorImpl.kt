package pro.apir.tko.domain.interactors.route.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.map
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.model.*
import javax.inject.Inject

class RouteSessionInteractorImpl @Inject constructor(private val sessionRepository: RouteSessionRepository,
                                                     private val userRepository: UserRepository) : RouteSessionInteractor {

    /**
     *
     * Return ID of Route of existing RouteSession
     *
     */
    override suspend fun getExistingSessionRouteId(): Either<Failure, Int?> {
        return withContext(Dispatchers.IO) {
            when (val userIdResult = userRepository.getUserId()) {
                is Either.Right -> {
                    val result = sessionRepository.checkSessionExists(userIdResult.b)
                    Either.Right(result)
                }
                is Either.Left -> userIdResult
            }
        }
    }

    /**
     *
     * Create initial RouteSessionModel from RouteModel
     *
     */
    override suspend fun getInitialSessionFromRoute(routeModel: RouteModel): Either<Failure, RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            when (val userIdResult = userRepository.getUserId()) {
                is Either.Right -> {
                    //CHECK EXISTING SESSION FOR THIS ROUTE AND USER
                    val existingSession = sessionRepository.checkSessionExists(userIdResult.b)

                    when (true) {
                        existingSession == routeModel.id -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_IN_PROGRESS)
                            val session = sessionRepository.resumeSession(userIdResult.b, route).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                            setPendingPoint(session.points)
                            Either.Right(session)
                        }
                        existingSession == null -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_DEFAULT)
                            Either.Right(route)
                        }
                        else -> {
                            val route = RouteSessionModel(routeModel, RouteStateConstants.ROUTE_TYPE_START_DISABLED)
                            Either.Right(route)
                        }
                    }

                }
                is Either.Left -> userIdResult
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
            when (val userIdResult = userRepository.getUserId()) {
                is Either.Right -> {
                    //CHECK EXISTING SESSION FOR THIS ROUTE AND USER
                    val existingSession = sessionRepository.checkSessionExists(userIdResult.b, routeSessionModel.routeId)
                    if (existingSession) {
                        val session = sessionRepository.resumeSession(userIdResult.b, routeSessionModel).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                        setPendingPoint(session.points)
                        Either.Right(session)
                    } else {
                        val session = sessionRepository.createSession(userIdResult.b, routeSessionModel).apply { this.state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS }
                        setPendingPoint(session.points)
                        Either.Right(session)
                    }
                }
                is Either.Left -> userIdResult
            }
        }

    }


    /**
     * Find and map existing route session to route list model
     */
    override suspend fun mapRouteListWithExisting(list: List<RouteModel>): Either<Failure, List<RouteModel>> {
        return withContext(Dispatchers.IO) {
            val routeId = getExistingSessionRouteId()
            routeId.map {
                list.map { model -> RouteModel(model, model.id == it) }
            }
        }
    }


    /**
     * Completes given route point model and return list with new states
     */
    override suspend fun completePoint(routeSessionModel: RouteSessionModel, routePointId: Long, photos: List<PhotoModel>): Either<Failure, RouteSessionModel> {
        return withContext(Dispatchers.IO) {
            //TODO LOAD PHOTOS TO SERVER?
            //TODO REQUEST TO SERVER?
            val newType =  RouteStateConstants.POINT_TYPE_COMPLETED
            sessionRepository.updatePoint(routePointId, newType)
            routeSessionModel.points.find { it.id == routePointId}?.type = newType

            setPendingPoint(routeSessionModel.points)

            val isCompleted = checkCompletion(routeSessionModel)
            if(isCompleted){
                routeSessionModel.state = RouteStateConstants.ROUTE_TYPE_COMPLETED
                sessionRepository.finishSession(routeSessionModel)
            }

            Either.Right(routeSessionModel)
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

    private fun checkCompletion(routeSessionModel: RouteSessionModel): Boolean{
        var flag = true
        routeSessionModel.points.forEach {
            if(it.type != RouteStateConstants.POINT_TYPE_COMPLETED)
                flag = false
        }
        return flag
    }

}