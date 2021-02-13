package pro.apir.tko.data.repository.route

import android.util.Log
import kotlinx.coroutines.CancellationException
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.api.RouteTrackApi
import pro.apir.tko.data.framework.network.model.request.RouteEnterStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteLeaveStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteTrackingStartRequest
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteStopTrackingResponse
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingDetailedResponse
import pro.apir.tko.data.mapper.TrackingFailureCodeMapper
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.failure.RouteTrackingFailure
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel
import pro.apir.tko.domain.model.route.RouteTrackingStopModel
import pro.apir.tko.domain.repository.route.RouteRepository
import pro.apir.tko.domain.repository.route.RouteSessionRepository
import pro.apir.tko.domain.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.repository.user.UserRepository
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class RouteSessionRepositoryImpl @Inject constructor(private val routePhotoRepository: RoutePhotoRepository,
                                                     private val routeRepository: RouteRepository,
                                                     private val routeTrackApi: RouteTrackApi,
                                                     private val userRepository: UserRepository,
                                                     private val trackingFailureCodeMapper: TrackingFailureCodeMapper,
                                                     private val credentialsManager: CredentialsManager) : RouteSessionRepository, BaseRepository(credentialsManager) {

    /**
     * Retrieves session from remote api by id
     */
    override suspend fun getRouteTrackingRemoteSessionById(sessionId: Long): Resource<RouteTrackingInfoModel> {
        val call = suspend { routeTrackApi.getRouteById(sessionId) }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }

        return requestTracking(call, map)
    }


    override suspend fun getCurrentRouteTrackingInfo(): Resource<RouteTrackingInfoModel?> {
        //REMOTE
        val call = suspend { routeTrackApi.getCurrentRoute() }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteInfo = requestTracking(call, map)

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
        val call = suspend { routeTrackApi.startRouteTracking(RouteTrackingStartRequest(routeId)) }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteResult = requestTracking(call, map)


        return remoteResult
    }

    override suspend fun finishRouteTracking(sessionId: Long): Resource<RouteTrackingInfoModel> {
        //TODO CHECK IF THERE IS CURRENT?
        val call = suspend { routeTrackApi.finishRouteTracking() }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteResult = requestTracking(call, map)

        return remoteResult
    }

    override suspend fun enterRouteStop(stopId: Long): Resource<RouteTrackingStopModel> {
        val call = suspend { routeTrackApi.enterStop(RouteEnterStopRequest(stopId)) }
        val map: (RouteStopTrackingResponse) -> RouteTrackingStopModel = { it.toModel() }
        return requestTracking(call, map)
    }

    override suspend fun leaveRouteStop(attachments: List<String>): Resource<RouteTrackingInfoModel> {

        val callStop = suspend { routeTrackApi.leaveStop(RouteLeaveStopRequest(attachments)) }
        val mapStop: (RouteStopTrackingResponse) -> RouteTrackingStopModel = { it.toModel() }
        val resultStop = requestTracking(callStop, mapStop)

        return when (resultStop) {
            is Resource.Error -> Resource.Error(resultStop.failure)
            is Resource.Success -> {
                val callResult = suspend { routeTrackApi.getCurrentRoute() }
                val mapResult: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
                return requestTracking(callResult, mapResult)
            }
        }
    }

    suspend fun <T, R> requestTracking(call: suspend () -> Response<T>, transform: (T) -> R): Resource<R> {
        return if (!credentialsManager.isRefreshTokenExpired()) {
            try {
                val call = call.invoke()
                val body = call.body()
                when (call.isSuccessful && body != null) {
                    true -> Resource.Success(transform(body))
                    false -> {
                        when (call.code()) {
                            in 400..499 -> {
                                //RETRIEVE CODE!
                                val error = call.errorBody()?.string()
                                Log.d("http", "error is $error")
                                Resource.Error(RouteTrackingFailure(trackingFailureCodeMapper.getFailureFromJSON(error)))
                            }
                            else -> {
                                Resource.Error(Failure.ServerError())
                            }
                        }
                    }
                }
            } catch (exception: Throwable) {
                Log.e("failure", "Failure: " + exception.localizedMessage)
                when (exception) {
                    is IOException -> {
                        Resource.Error(Failure.Ignore)
                    }
                    //WARNING
                    is CancellationException -> {
                        Resource.Error(Failure.Ignore)
                    }
                    else -> {
                        Resource.Error(Failure.ServerError())
                    }
                }
            }
        } else {
            Resource.Error(Failure.RefreshTokenExpired)
        }
    }

}