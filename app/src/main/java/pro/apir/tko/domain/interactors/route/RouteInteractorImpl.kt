package pro.apir.tko.domain.interactors.route

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.route.RouteRepository
import javax.inject.Inject

class RouteInteractorImpl @Inject constructor(private val routeRepository: RouteRepository, private val routeSessionInteractor: RouteSessionInteractor) : RouteInteractor {
    override suspend fun getRoutesList(page: Int, pageSize: Int) =
            withContext(Dispatchers.IO) {
                when(val routeListResult = routeRepository.getRoutesList(page, pageSize)){
                    is Either.Left -> routeListResult
                    is Either.Right -> routeSessionInteractor.mapRouteListWithExisting(routeListResult.b)
                }
            }
}