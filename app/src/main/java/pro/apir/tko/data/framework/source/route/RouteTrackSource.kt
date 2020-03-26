package pro.apir.tko.data.framework.source.route

import pro.apir.tko.data.framework.network.api.RouteTrackApi
import pro.apir.tko.data.framework.network.model.request.RouteEnterStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteLeaveStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteTrackingStartRequest
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteStopTrackingResponse
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingDetailedResponse
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingResponse
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 19.03.2020
 * Project: tko-android
 */
class RouteTrackSource @Inject constructor(retrofit: Retrofit): RouteTrackApi{

    val api by lazy { retrofit.create(RouteTrackApi::class.java) }

    override suspend fun getRouteById(id: Long): Response<RouteTrackingDetailedResponse> {
        return api.getRouteById(id)
    }

    override suspend fun getCurrentRoute(): Response<RouteTrackingResponse> {
        return api.getCurrentRoute()
    }

    override suspend fun startRouteTracking(request: RouteTrackingStartRequest): Response<RouteTrackingResponse> {
        return api.startRouteTracking(request)
    }

    override suspend fun finishRouteTracking(): Response<RouteTrackingResponse> {
        return api.finishRouteTracking()
    }

    override suspend fun enterStop(request: RouteEnterStopRequest): Response<RouteStopTrackingResponse> {
        return api.enterStop(request)
    }

    override suspend fun leaveStop(request: RouteLeaveStopRequest): Response<RouteStopTrackingResponse> {
        return api.leaveStop(request)
    }

}