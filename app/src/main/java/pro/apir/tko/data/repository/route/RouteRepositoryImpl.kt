package pro.apir.tko.data.repository.route

import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.RouteApi
import pro.apir.tko.data.repository.BaseRepository
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(private val tokenManager: TokenManager, private val routeApi: RouteApi) : RouteRepository, BaseRepository(tokenManager) {
    override suspend fun getRoutesList(page: Int, pageSize: Int) = request({ routeApi.getRoutesList(page, pageSize) }, { it.results.map { item -> item.toModel()  } })
}