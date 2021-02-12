package pro.apir.tko.domain.interactors.route

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractor
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.repository.route.RouteRepository
import javax.inject.Inject

class RouteInteractorImpl @Inject constructor(private val routeRepository: RouteRepository, private val routeSessionInteractor: RouteSessionInteractor) : RouteInteractor {
    override suspend fun getRoutesList(page: Int, pageSize: Int) =
            withContext(Dispatchers.IO) {
                val routeListResult = routeRepository.getRoutesList(page, pageSize)
                return@withContext processRoutes(routeListResult)
            }

    override suspend fun searchRoutes(search: String) =
            withContext(Dispatchers.IO) {
                val routeListResult = routeRepository.searchRoutes(search)
                return@withContext processRoutes(routeListResult)
            }

    private suspend fun processRoutes(routeListResult: Either<Failure, List<RouteModel>>) = when (routeListResult) {
        is Either.Left -> routeListResult
        is Either.Right -> routeSessionInteractor.mapRouteListWithExisting(routeListResult.b)
    }

}