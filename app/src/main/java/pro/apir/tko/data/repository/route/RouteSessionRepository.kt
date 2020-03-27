package pro.apir.tko.data.repository.route

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionRepository {
    //new

    suspend fun getRouteTrackingRemoteSessionById(sessionId: Long): Either<Failure, RouteTrackingInfoModel>

    suspend fun getCurrentRouteTrackingInfo():  Either<Failure, RouteTrackingInfoModel?>

    suspend fun startRouteTrackingSession(): Either<Failure, RouteSessionModel>

    suspend fun getCurrentRouteTrackingSession(): Either<Failure, RouteSessionModel>

    suspend fun finishRouteTrackingSession(): Either<Failure, RouteSessionModel>


    //old
    //check existing session
//    suspend fun checkSessionExists(userId: Int): Either<Failure, RouteTrackingInfoModel?>
//
//    suspend fun checkSessionExists(userId: Int, routeId: Int): Either<Failure, Boolean>

    //get (start or resume) session
    suspend fun createSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel

    suspend fun resumeSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel

    suspend fun finishSession(routeSessionModel: RouteSessionModel): RouteSessionModel

    suspend fun updatePoint(pointId: Long, attachedPhotos: List<String>, type: Int): Either<Failure, Boolean>

    //

    suspend fun updateSession(routeSessionModel: RouteSessionModel): RouteSessionModel

}