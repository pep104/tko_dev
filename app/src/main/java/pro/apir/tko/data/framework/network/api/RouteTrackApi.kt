package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.request.RouteEnterStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteLeaveStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteTrackingStartRequest
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteStopTrackingResponse
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingDetailedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by Антон Сарматин
 * Date: 19.03.2020
 * Project: tko-android
 */
interface RouteTrackApi {

    //
    @GET("/fleet/tracks/{id}/")
    suspend fun getRouteById(@Path("id") id: Long): Response<RouteTrackingDetailedResponse>


    //Route
    @GET("fleet/tracks/current/")
    suspend fun getCurrentRoute(): Response<RouteTrackingDetailedResponse>


    @POST("fleet/tracks/start/")
    suspend fun startRouteTracking(@Body request: RouteTrackingStartRequest): Response<RouteTrackingDetailedResponse>

    @POST("fleet/tracks/finish/")
    suspend fun finishRouteTracking(): Response<RouteTrackingDetailedResponse>

    //Stops
    @POST("fleet/tracks/enter-stop/")
    suspend fun enterStop(@Body request: RouteEnterStopRequest ): Response<RouteStopTrackingResponse>

    @POST("fleet/tracks/leave-stop/")
    suspend fun leaveStop(@Body request: RouteLeaveStopRequest): Response<RouteStopTrackingResponse>

}