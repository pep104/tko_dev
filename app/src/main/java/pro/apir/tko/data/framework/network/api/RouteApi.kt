package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.response.RouteListResponse
import pro.apir.tko.data.framework.network.model.response.data.RouteData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
interface RouteApi {

    @GET("fleet/routes/")
    suspend fun getRoutesList(@Query("page") page: Int, @Query("page_size") pageSize: Int): Response<RouteListResponse>

    @GET("fleet/routes/")
    suspend fun searchRoutesList(@Query("search") search: String): Response<RouteListResponse>

    @GET("fleet/routes/{id}")
    suspend fun getRoute(@Path("id") id: Long): Response<RouteData>

}