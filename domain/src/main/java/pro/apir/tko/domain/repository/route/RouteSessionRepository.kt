package pro.apir.tko.domain.repository.route

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel
import pro.apir.tko.domain.model.route.RouteTrackingStopModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionRepository {
    //new

    suspend fun getRouteTrackingRemoteSessionById(sessionId: Long): Either<Failure, RouteTrackingInfoModel>

    suspend fun getCurrentRouteTrackingInfo():  Either<Failure, RouteTrackingInfoModel?>

    suspend fun startRouteTracking(routeId: Long): Either<Failure, RouteTrackingInfoModel>

    suspend fun finishRouteTracking(sessionId: Long): Either<Failure, RouteTrackingInfoModel>

    suspend fun enterRouteStop(stopId: Long): Either<Failure, RouteTrackingStopModel>

    suspend fun leaveRouteStop(attachments: List<String>): Either<Failure, RouteTrackingInfoModel>

}