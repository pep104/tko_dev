package pro.apir.tko.data.framework.source.route

import pro.apir.tko.data.framework.network.api.RouteApi
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
class RouteSource @Inject constructor(private val retrofit: Retrofit) : RouteApi {

    private val api by lazy { retrofit.create(RouteApi::class.java) }

    override suspend fun getRoutesList(page: Int, pageSize: Int) =
            api.getRoutesList(page, pageSize)

    override suspend fun searchRoutesList(search: String) =
            api.searchRoutesList(search)

    override suspend fun getRoute(id: Long) =
            api.getRoute(id)
}