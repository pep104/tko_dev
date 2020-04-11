package pro.apir.tko.data.repository.route

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.RouteApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.RouteModel
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(private val tokenManager: TokenManager, private val routeApi: RouteApi) : RouteRepository, BaseRepository(tokenManager) {

    override suspend fun getRoutesList(page: Int, pageSize: Int) = request({ routeApi.getRoutesList(page, pageSize) }, { it.results.map { item -> item.toModel() } })

    override suspend fun searchRoutes(search: String): Either<Failure, List<RouteModel>> = request(
            {
                routeApi.searchRoutesList(search)
            },
            {
                it.results.map { item -> item.toModel() }
            })

    override suspend fun getRoute(id: Long): Either<Failure, RouteModel> = request({ routeApi.getRoute(id) }, { it.toModel() })
}