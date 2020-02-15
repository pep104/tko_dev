package pro.apir.tko.domain.interactors.route

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pro.apir.tko.data.repository.route.RouteSessionRepository
import javax.inject.Inject

class RouteSessionInteractorImpl @Inject constructor(private val sessionRepository: RouteSessionRepository) : RouteSessionInteractor {

    override suspend fun checkSessionExists(routeId: Int): Boolean {
        //TODO
        val userID = 1

        return sessionRepository.checkSessionExists(userID, routeId, LocalDate.now().format(DateTimeFormatter.ISO_DATE))
    }

}