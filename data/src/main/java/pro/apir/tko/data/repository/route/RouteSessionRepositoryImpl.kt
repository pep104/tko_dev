package pro.apir.tko.data.repository.route

import android.util.Log
import kotlinx.coroutines.CancellationException
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
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
    override suspend fun getRouteTrackingRemoteSessionById(sessionId: Long): Either<Failure, RouteTrackingInfoModel> {
        val call = suspend { routeTrackApi.getRouteById(sessionId) }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }

        return requestTracking(call, map)
    }


    override suspend fun getCurrentRouteTrackingInfo(): Either<Failure, RouteTrackingInfoModel?> {
        //REMOTE
        val call = suspend { routeTrackApi.getCurrentRoute() }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteInfo = requestTracking(call, map)

        when (remoteInfo) {
            is Either.Left -> {
                //If Remote call is Failure check is RouteTrackingFailure (no route tracked)
                return if (remoteInfo.a is RouteTrackingFailure)
                    Either.Right(null)
                else
                    remoteInfo
            }
            is Either.Right -> {
                val localPhotos = routePhotoRepository.getPhotosBySession(remoteInfo.b.sessionId)
                return Either.Right(remoteInfo.b.apply { this.localPhotos.addAll(localPhotos) })
            }
        }
    }

    override suspend fun startRouteTracking(routeId: Long): Either<Failure, RouteTrackingInfoModel> {
        val call = suspend { routeTrackApi.startRouteTracking(RouteTrackingStartRequest(routeId)) }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteResult = requestTracking(call, map)


        return remoteResult
    }

    override suspend fun finishRouteTracking(sessionId: Long): Either<Failure, RouteTrackingInfoModel> {
        //TODO CHECK IF THERE IS CURRENT?
        val call = suspend { routeTrackApi.finishRouteTracking() }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
        val remoteResult = requestTracking(call, map)

        return remoteResult
    }

    override suspend fun enterRouteStop(stopId: Long): Either<Failure, RouteTrackingStopModel> {
        val call = suspend { routeTrackApi.enterStop(RouteEnterStopRequest(stopId)) }
        val map: (RouteStopTrackingResponse) -> RouteTrackingStopModel = { it.toModel() }
        return requestTracking(call, map)
    }

    override suspend fun leaveRouteStop(attachments: List<String>): Either<Failure, RouteTrackingInfoModel> {

        val callStop = suspend { routeTrackApi.leaveStop(RouteLeaveStopRequest(attachments)) }
        val mapStop: (RouteStopTrackingResponse) -> RouteTrackingStopModel = { it.toModel() }
        val resultStop = requestTracking(callStop, mapStop)

        return when (resultStop) {
            is Either.Left -> Either.Left(resultStop.a)
            is Either.Right -> {
                val callResult = suspend { routeTrackApi.getCurrentRoute() }
                val mapResult: (RouteTrackingDetailedResponse) -> RouteTrackingInfoModel = { it.toModel() }
                return requestTracking(callResult, mapResult)
            }
        }
    }

    suspend fun <T, R> requestTracking(call: suspend () -> Response<T>, transform: (T) -> R): Either<Failure, R> {
        return if (!credentialsManager.isRefreshTokenExpired()) {
            try {
                val call = call.invoke()
                val body = call.body()
                when (call.isSuccessful && body != null) {
                    true -> Either.Right(transform(body))
                    false -> {
                        when (call.code()) {
                            in 400..499 -> {
                                //RETRIEVE CODE!
                                val error = call.errorBody()?.string()
                                Log.d("http", "error is $error")
                                Either.Left(RouteTrackingFailure(trackingFailureCodeMapper.getFailureFromJSON(error)))
                            }
                            else -> {
                                Either.Left(Failure.ServerError())
                            }
                        }
                    }
                }
            } catch (exception: Throwable) {
                Log.e("failure", "Failure: " + exception.localizedMessage)
                when (exception) {
                    is IOException -> {
                        Either.Left(Failure.Ignore)
                    }
                    //WARNING
                    is CancellationException -> {
                        Either.Left(Failure.Ignore)
                    }
                    else -> {
                        Either.Left(Failure.ServerError())
                    }
                }
            }
        } else {
            Either.Left(Failure.RefreshTokenExpired)
        }
    }

}