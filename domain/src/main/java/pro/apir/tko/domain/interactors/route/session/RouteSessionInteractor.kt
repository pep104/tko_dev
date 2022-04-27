package pro.apir.tko.domain.interactors.route.session

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionInteractor {

    suspend fun getCurrentTrackingInfo(): Resource<RouteTrackingInfoModel?>


    suspend fun getSessionFromRoute(routeModel: RouteModel): Flow<Resource<RouteSessionModel>>

    suspend fun startSession(routeSessionModel: RouteSessionModel): Resource<RouteSessionModel>

    suspend fun finishSession(routeSessionModel: RouteSessionModel): Resource<RouteSessionModel>

    suspend fun mapRouteListWithExisting(list: List<RouteModel>): Resource<List<RouteModel>>

    suspend fun completePoint(routeSessionModel: RouteSessionModel, routePointId: Long): Resource<RouteSessionModel>

}