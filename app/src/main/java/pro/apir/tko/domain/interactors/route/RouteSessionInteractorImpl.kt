package pro.apir.tko.domain.interactors.route

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants
import javax.inject.Inject

class RouteSessionInteractorImpl @Inject constructor(private val sessionRepository: RouteSessionRepository) : RouteSessionInteractor {

    override suspend fun getSession(routeModel: RouteModel): Either<Failure, RouteSessionModel> {
        val userId = 1 //TODO
        //CHECK EXISTING SESSION FOR THIS ROUTE AND USER
        val isSessionExists = sessionRepository.checkSessionExists(userId, routeModel.id, LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        val state = if (isSessionExists) RouteStateConstants.ROUTE_TYPE_PENDING else RouteStateConstants.ROUTE_TYPE_DEFAULT

        return Either.Right(RouteSessionModel(routeModel, state))
    }
}