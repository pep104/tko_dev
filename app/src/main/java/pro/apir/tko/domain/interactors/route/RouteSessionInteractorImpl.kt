package pro.apir.tko.domain.interactors.route

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants
import javax.inject.Inject

class RouteSessionInteractorImpl @Inject constructor(private val sessionRepository: RouteSessionRepository,
                                                     private val userRepository: UserRepository) : RouteSessionInteractor {

    /**
     *
     * Create initial RouteSessionModel from RouteModel
     *
     */
    override suspend fun getInitialSessionFromRoute(routeModel: RouteModel): Either<Failure, RouteSessionModel> {
        return when (val userIdResult = userRepository.getUserId()) {
            is Either.Right -> {
                //CHECK EXISTING SESSION FOR THIS ROUTE AND USER
                val isSessionExists = sessionRepository.checkSessionExists(userIdResult.b, routeModel.id, LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                val state = if (isSessionExists) RouteStateConstants.ROUTE_TYPE_PENDING else RouteStateConstants.ROUTE_TYPE_DEFAULT

                Either.Right(RouteSessionModel(routeModel, state))
            }
            is Either.Left -> userIdResult
        }
    }

    /**
     *
     *  Start RouteSessionModel or Resume it with saved progress
     *
     */
    override suspend fun startSession(routeSessionModel: RouteSessionModel): Either<Failure, RouteSessionModel> {
//        return test(routeSessionModel)


        return when (val userIdResult = userRepository.getUserId()) {
            is Either.Right -> {
                //CHECK EXISTING SESSION FOR THIS ROUTE AND USER
                val isSessionExists = sessionRepository.checkSessionExists(userIdResult.b, routeSessionModel.routeId, LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                if (isSessionExists) {
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

    //Test return
    private fun test(routeSessionModel: RouteSessionModel): Either<Failure, RouteSessionModel> {
        var lastCompleted = 0
        routeSessionModel.points.forEachIndexed { index, routePointModel ->
            if (index != 0 && index != routeSessionModel.points.size - 1 && index < 5) {
                routePointModel.type = RouteStateConstants.POINT_TYPE_COMPLETED
                lastCompleted = index
            }
        }

        if (lastCompleted + 1 < routeSessionModel.points.size - 1) {
            routeSessionModel.points[lastCompleted + 1].type = RouteStateConstants.POINT_TYPE_PENDING
        }

        return Either.Right(routeSessionModel.apply { state = RouteStateConstants.ROUTE_TYPE_IN_PROGRESS })
    }
}