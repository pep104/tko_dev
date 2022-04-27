package pro.apir.tko.domain.interactors.route

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.data.Resource
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

    private suspend fun processRoutes(routeListResult: Resource<List<RouteModel>>) = when (routeListResult) {
        is Resource.Error -> routeListResult
        is Resource.Success -> routeSessionInteractor.mapRouteListWithExisting(routeListResult.data)
    }

}