package pro.apir.tko.data.repository.route

import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.mapFailure
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.data.framework.network.api.RouteTrackApi
import pro.apir.tko.data.framework.network.model.request.RouteEnterStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteLeaveStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteTrackingStartRequest
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteStopTrackingResponse
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingDetailedResponse
import pro.apir.tko.data.mapper.TrackingFailureCodeMapper
import pro.apir.tko.domain.failure.RouteTrackingFailure
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel
import pro.apir.tko.domain.model.route.RouteTrackingStopModel
import pro.apir.tko.domain.repository.route.RouteRepository
import pro.apir.tko.domain.repository.route.RouteSessionRepository
import pro.apir.tko.domain.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.repository.user.UserRepository
import javax.inject.Inject

class RouteSessionRepositoryImpl @Inject constructor(private val routePhotoRepository: RoutePhotoRepository,
                                                     private val routeRepository: RouteRepository,
                                                     private val routeTrackApi: RouteTrackApi,
                                                     private val userRepository: UserRepository,
                                                     private val trackingFailureCodeMapper: TrackingFailureCodeMapper) : RouteSessionRepository {

    /**
     * Retrieves session from remote api by id
     */
    override suspend fun getRouteTrackingRemoteSessionById(sessionId: Long): Resource<RouteTrackingInfoModel> {
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }

        return routeTrackApi.getRouteById(sessionId).toResult(map).mapFailure { checkTrackingFailure(it) }
    }


    override suspend fun getCurrentRouteTrackingInfo(): Resource<RouteTrackingInfoModel?> {
        //REMOTE
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteInfo = routeTrackApi.getCurrentRoute().toResult(map).mapFailure { checkTrackingFailure(it) }

        when (remoteInfo) {
            is Resource.Error -> {
                //If Remote call is Failure check is RouteTrackingFailure (no route tracked)
                return if (remoteInfo.failure is RouteTrackingFailure)
                    Resource.Success(null)
                else
                    remoteInfo
            }
            is Resource.Success -> {
                val localPhotos = routePhotoRepository.getPhotosBySession(remoteInfo.data.sessionId)
                return Resource.Success(remoteInfo.data.apply { this.localPhotos.addAll(localPhotos) })
            }
        }
    }

    override suspend fun startRouteTracking(routeId: Long): Resource<RouteTrackingInfoModel> {
        val requestParams = RouteTrackingStartRequest(routeId)
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteResult = routeTrackApi.startRouteTracking(requestParams).toResult(map).mapFailure { checkTrackingFailure(it) }


        return remoteResult
    }

    override suspend fun finishRouteTracking(sessionId: Long): Resource<RouteTrackingInfoModel> {
        //TODO CHECK IF THERE IS CURRENT?
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteResult = routeTrackApi.finishRouteTracking().toResult(map).mapFailure { checkTrackingFailure(it) }

        return remoteResult
    }

    override suspend fun enterRouteStop(stopId: Long): Resource<RouteTrackingStopModel> {
        val params = RouteEnterStopRequest(stopId)
        val map: (RouteStopTrackingResponse) -> RouteTrackingStopModel = { it.toModel() }
        return routeTrackApi.enterStop(params).toResult(map).mapFailure { checkTrackingFailure(it) }
    }

    override suspend fun leaveRouteStop(attachments: List<String>): Resource<RouteTrackingInfoModel> {
        val params = RouteLeaveStopRequest(attachments)
        val mapStop: (RouteStopTrackingResponse) -> RouteTrackingStopModel = { it.toModel() }
        val resultStop = routeTrackApi.leaveStop(params).toResult(mapStop).mapFailure { checkTrackingFailure(it) }

        return when (resultStop) {
            is Resource.Error -> Resource.Error(resultStop.failure)
            is Resource.Success -> {
                val callResult = suspend { routeTrackApi.getCurrentRoute() }
                val mapResult: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
                return routeTrackApi.getCurrentRoute().toResult(mapResult).mapFailure { checkTrackingFailure(it) }
            }
        }
    }

    fun checkTrackingFailure(failure: Failure): Failure {
        if (failure is Failure.ServerError) {
            if (failure.statusCode in 400..499) {
                return RouteTrackingFailure(trackingFailureCodeMapper.getFailureFromJSON(failure.errorMessage))
            }
        }
        return failure
    }

}