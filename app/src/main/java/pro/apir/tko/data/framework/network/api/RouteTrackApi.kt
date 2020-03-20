package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.BaseResponse
import pro.apir.tko.data.framework.network.model.request.RouteEnterStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteLeaveStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteTrackingStartRequest
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Антон Сарматин
 * Date: 19.03.2020
 * Project: tko-android
 */
interface RouteTrackApi {

    //Route
    @GET("fleet/tracks/current/")
    suspend fun getCurrentRoute(): Response<RouteTrackingResponse>


    @POST("fleet/tracks/start/")
    suspend fun startRouteTracking(@Body request: RouteTrackingStartRequest): Response<RouteTrackingResponse>

    @POST("fleet/tracks/finish/")
    suspend fun finishRouteTracking(): Response<RouteTrackingResponse>

    //Stops
    @POST("fleet/tracks/enter-stop/")
    suspend fun enterStop(@Body request: RouteEnterStopRequest ): Response<BaseResponse>

    @POST("fleet/tracks/leave-stop/")
    suspend fun leaveStop(@Body request: RouteLeaveStopRequest): Response<BaseResponse>

}