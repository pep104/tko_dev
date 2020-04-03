package pro.apir.tko.domain.interactors.route.session

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionInteractor {

    suspend fun getCurrentTrackingInfo(): Either<Failure, RouteTrackingInfoModel?>


    suspend fun getSessionFromRoute(routeModel: RouteModel): Flow<Either<Failure, RouteSessionModel>>

    suspend fun startSession(routeSessionModel: RouteSessionModel): Either<Failure, RouteSessionModel>

    suspend fun finishSession(routeSessionModel: RouteSessionModel): Either<Failure, RouteSessionModel>

    suspend fun mapRouteListWithExisting(list: List<RouteModel>): Either<Failure, List<RouteModel>>

    suspend fun completePoint(routeSessionModel: RouteSessionModel, routePointId: Long): Either<Failure, RouteSessionModel>

}