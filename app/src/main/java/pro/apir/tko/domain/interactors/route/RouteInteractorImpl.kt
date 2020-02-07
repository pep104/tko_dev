package pro.apir.tko.domain.interactors.route

import pro.apir.tko.data.repository.route.RouteRepository
import javax.inject.Inject

class RouteInteractorImpl @Inject constructor(private val routeRepository: RouteRepository) : RouteInteractor {
    override suspend fun getRoutesList(page: Int, pageSize: Int) =
            routeRepository.getRoutesList(page, pageSize)
}