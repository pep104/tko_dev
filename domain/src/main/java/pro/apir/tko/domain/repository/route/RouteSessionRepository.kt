package pro.apir.tko.domain.repository.route

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel
import pro.apir.tko.domain.model.route.RouteTrackingStopModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionRepository {
    //new

    suspend fun getRouteTrackingRemoteSessionById(sessionId: Long): Resource<RouteTrackingInfoModel>

    suspend fun getCurrentRouteTrackingInfo(): Resource<RouteTrackingInfoModel?>

    suspend fun startRouteTracking(routeId: Long): Resource<RouteTrackingInfoModel>

    suspend fun finishRouteTracking(sessionId: Long): Resource<RouteTrackingInfoModel>

    suspend fun enterRouteStop(stopId: Long): Resource<RouteTrackingStopModel>

    suspend fun leaveRouteStop(attachments: List<String>): Resource<RouteTrackingInfoModel>

}