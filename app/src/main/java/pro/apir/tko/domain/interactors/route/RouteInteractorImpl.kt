package pro.apir.tko.domain.interactors.route

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.data.repository.route.RouteRepository
import javax.inject.Inject

class RouteInteractorImpl @Inject constructor(private val routeRepository: RouteRepository) : RouteInteractor {
    override suspend fun getRoutesList(page: Int, pageSize: Int) =
            withContext(Dispatchers.IO) {
                routeRepository.getRoutesList(page, pageSize)
            }
}