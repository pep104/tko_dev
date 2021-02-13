package pro.apir.tko.data.repository.route

import pro.apir.tko.core.data.Resource
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.api.RouteApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.repository.route.RouteRepository
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(private val credentialsManager: CredentialsManager, private val routeApi: RouteApi) : RouteRepository, BaseRepository(credentialsManager) {

    override suspend fun getRoutesList(page: Int, pageSize: Int) = routeApi.getRoutesList(page, pageSize).toResult { it.results.map { item -> item.toModel() } }

    override suspend fun searchRoutes(search: String): Resource<List<RouteModel>> = routeApi.searchRoutesList(search).toResult {
        it.results.map { item -> item.toModel() }
    }

    override suspend fun getRoute(id: Long): Resource<RouteModel> = routeApi.getRoute(id).toResult { it.toModel() }
}