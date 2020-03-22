package pro.apir.tko.domain.interactors.route.session

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RouteSessionModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionInteractor {

    suspend fun getExistingSessionRouteId(): Either<Failure, Int?>

    suspend fun getInitialSessionFromRoute(routeModel: RouteModel): Either<Failure, RouteSessionModel>

    suspend fun startSession(routeSessionModel: RouteSessionModel): Either<Failure, RouteSessionModel>

    suspend fun mapRouteListWithExisting(list: List<RouteModel>): Either<Failure, List<RouteModel>>

    suspend fun updateSession(routeSessionModel: RouteSessionModel): RouteSessionModel

    suspend fun startPoint(routeSessionModel: RouteSessionModel, routePointId: Long): Either<Failure, RouteSessionModel>

    suspend fun completePoint(routeSessionModel: RouteSessionModel, routePointId: Long): Either<Failure, RouteSessionModel>

}