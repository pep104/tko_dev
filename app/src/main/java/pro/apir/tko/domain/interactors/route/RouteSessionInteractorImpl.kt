package pro.apir.tko.domain.interactors.route

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants
import javax.inject.Inject

class RouteSessionInteractorImpl @Inject constructor(private val sessionRepository: RouteSessionRepository,
                                                     private val userRepository: UserRepository) : RouteSessionInteractor {

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

    override suspend fun startSession(routeSessionModel: RouteSessionModel): Either<Failure, RouteSessionModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}