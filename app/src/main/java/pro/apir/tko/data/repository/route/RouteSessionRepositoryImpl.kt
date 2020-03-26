package pro.apir.tko.data.repository.route

import android.util.Log
import kotlinx.coroutines.CancellationException
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.RouteTrackApi
import pro.apir.tko.data.framework.network.model.request.RouteEnterStopRequest
import pro.apir.tko.data.framework.network.model.request.RouteLeaveStopRequest
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingDetailedResponse
import pro.apir.tko.data.framework.network.model.response.routetracking.RouteTrackingResponse
import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.room.dao.PointDao
import pro.apir.tko.data.framework.room.dao.RouteSessionDao
import pro.apir.tko.data.framework.room.entity.PointEntity
import pro.apir.tko.data.framework.room.entity.RouteSessionEntity
import pro.apir.tko.data.framework.room.entity.relation.RouteSessionWithPoints
import pro.apir.tko.data.mapper.PhotoTypeMapper
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.failure.RouteTrackingFailure
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.domain.model.route.RouteTrackingModel
import pro.apir.tko.domain.model.route.remote.RouteTrackingDetailedRemoteModel
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class RouteSessionRepositoryImpl @Inject constructor(private val routeSessionDao: RouteSessionDao,
                                                     private val routePointDao: PointDao,
                                                     private val routePhotoDao: PhotoDao,
                                                     private val photoTypeMapper: PhotoTypeMapper,
                                                     private val routeTrackApi: RouteTrackApi,
                                                     private val tokenManager: TokenManager) : RouteSessionRepository, BaseRepository(tokenManager) {

    //TODO OFFSET FROM BACKEND
    private val offsetHours = 3

    //NEW

    /**
     * Retrieves session from remote api by id
     */
    override suspend fun getRouteTrackingRemoteSessionById(sessionId: Long): Either<Failure, RouteTrackingDetailedRemoteModel> {
        val call = suspend { routeTrackApi.getRouteById(sessionId) }
        val map: (RouteTrackingDetailedResponse) -> RouteTrackingDetailedRemoteModel = { it.toModel() }

        return requestTracking(call, map)
    }


    //OLD

    /**
     *
     *  Checks if there is session state record for this User by this day (today from 02:00 to next day 02:00)
     *
     */
    override suspend fun checkSessionExists(userId: Int): Either<Failure, RouteTrackingModel?> {
        val call = suspend { routeTrackApi.getCurrentRoute() }
        val map: (RouteTrackingResponse) -> RouteTrackingModel = { it.toModel() }
        val remoteResult = requestTracking(call, map)

        if (remoteResult is Either.Left && remoteResult.a is RouteTrackingFailure) {
            return Either.Right(null)
        }

        //TODO CHECK IF LOCAL SESSION EXISTS
        // IF NOT -> getTracking from api, create session, return remoteResult
        // IF TRUE -> return remoteResult

        return remoteResult
    }
    //    override suspend fun checkSessionExists(userId: Int): Int? {
//        val dateRange = getCurrentDateRange(offsetHours)
//        val results = routeSessionDao.getExistingSession(userId, dateRange.first, dateRange.second)
//
//        return if (results.isEmpty()) {
//            null
//        } else {
//            val result = results.last()
//            if (result.session.isCompleted) {
//                null
//            } else {
//                result.session.routeId
//            }
//        }
//    }

    /**
     *
     *  Checks if there is session state record for this User and Route ID by this day (today from 02:00 to next day 02:00)
     *
     */
    override suspend fun checkSessionExists(userId: Int, routeId: Int): Either<Failure, Boolean> {
        val res = checkSessionExists(userId)
        return when (res) {
            is Either.Left -> res
            is Either.Right -> {
                return Either.Right(res.b?.routeId == routeId.toLong())
            }
        }
    }
    //    override suspend fun checkSessionExists(userId: Int, routeId: Int): Boolean = checkSessionExists(userId) == routeId

    /**
     *
     *  Creates new state record for RouteSessionModel and starts this RouteSessionModel
     *
     */
    //TODO TO START? WITH API INTERACTION
    override suspend fun createSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel {
        //Insert Session
        //TODO OFFSET FROM BACKEND
        val sessionEntity = RouteSessionEntity(null, userId, routeSessionModel.routeId, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(offsetHours)), false)
        val sessionId = routeSessionDao.insertSession(sessionEntity)
        //Insert Points
        val newPoints = mutableListOf<RoutePointModel>()
        routeSessionModel.points.forEach {
            val pointEntity = PointEntity(null, it.pointId, it.entityId, it.type
                    ?: RouteStateConstants.POINT_TYPE_DEFAULT, sessionId)
            val id = routeSessionDao.insertPoint(pointEntity)
            newPoints.add(RoutePointModel(id, it))
        }

        //Return result
        return RouteSessionModel(sessionId, newPoints, routeSessionModel)
    }

    /**
     *
     *  Resume existing RouteSessionModel with saved state (progress of RouteSession)
     *
     */
    override suspend fun resumeSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel {
        val dateRange = getCurrentDateRange(offsetHours)
        val savedSessionEntityWithPoints = routeSessionDao.getSession(userId, routeSessionModel.routeId, dateRange.first, dateRange.second)

        return if (savedSessionEntityWithPoints.isNotEmpty()) {
            val routeSessionWithPoints = savedSessionEntityWithPoints.last()
            mapRouteSessionEntityToModel(routeSessionModel, routeSessionWithPoints)

        } else {
            createSession(userId, routeSessionModel)
        }

    }

    /**
     * Updates session
     */
    override suspend fun updateSession(routeSessionModel: RouteSessionModel): RouteSessionModel {
        val id = routeSessionModel.sessionId
        return if (id != null) {
            mapRouteSessionEntityToModel(routeSessionModel, routeSessionDao.getSessionWithPoints(id))

        } else {
            routeSessionModel
        }

    }

    /**
     * Finishes session
     */
    //TODO WITH NETWORK
    override suspend fun finishSession(routeSessionModel: RouteSessionModel): RouteSessionModel {
        routeSessionModel.sessionId?.let {
            val entity = routeSessionDao.getSession(it)
            routeSessionDao.updateSession(RouteSessionEntity(entity.id, entity.userId, entity.routeId, entity.dateLong, true))
        }
        return updateSession(routeSessionModel)
    }

    /**
     * Get current day dates range
     * Route finish time is 02:00
     */
    private fun getCurrentDateRange(offsetHours: Int): Pair<Long, Long> {
        val now = LocalDateTime.now()
        val offset = ZoneOffset.ofHours(offsetHours)
        return if (now.hour <= 2) {
            val start = LocalDateTime.of(now.minusDays(1).toLocalDate(), LocalTime.of(2, 0)).toEpochSecond(offset)
            val end = LocalDateTime.of(now.toLocalDate(), LocalTime.of(2, 0)).toEpochSecond(offset)
            Pair(start, end)
        } else {
            val start = LocalDateTime.of(now.toLocalDate(), LocalTime.of(2, 0)).toEpochSecond(offset)
            val end = LocalDateTime.of(now.plusDays(1).toLocalDate(), LocalTime.of(2, 0)).toEpochSecond(offset)
            Pair(start, end)
        }
    }

    /**
     * Updates this route point with new type
     */
    //TODO COMPLETE POINT AT BACKEND
    override suspend fun updatePoint(pointId: Long, attachedPhotos: List<String>, type: Int): Either<Failure, Boolean> {
        //Разделить старт точки и завершение в логике приложения?
        //Start current point
        val startResult = request({ routeTrackApi.enterStop(RouteEnterStopRequest(pointId.toInt())) }, { true })
        if (startResult is Either.Left)
            return startResult

        //Finish current point
        val completeResult = request({ routeTrackApi.leaveStop(RouteLeaveStopRequest(attachedPhotos)) }, { true })
        if (completeResult is Either.Left)
            return completeResult

        //Update local DB
        val point = routePointDao.getPoint(pointId)
        val newPoint = PointEntity(point.id, point.pointId, point.entityId, type, point.sessionId)
        routePointDao.updatePoint(newPoint)

        return Either.Right(true)
    }


    //TODO to mapper class
    private fun mapRouteSessionEntityToModel(routeSessionModel: RouteSessionModel, routeSessionWithPoints: RouteSessionWithPoints): RouteSessionModel {
        val newPoints = mutableListOf<RoutePointModel>()
        routeSessionModel.points.forEach { pointSession ->

            val saved = routeSessionWithPoints.points.find { pointEntity -> pointEntity.point.pointId == pointSession.pointId }
            if (saved != null && saved.point.id != null) {
                val photos = routePhotoDao.selectAllPhotosByPoint(saved.point.id).map { photoTypeMapper.toModel(it) }
                Log.d("photos", "at session interactor: $photos")
                val model = RoutePointModel(saved.point.id, saved.point.type, photos, pointSession)
                Log.d("point", "model: $model")
                newPoints.add(model)
            }

        }

        return RouteSessionModel(routeSessionWithPoints.session.id, newPoints, routeSessionModel)
    }


    //FIXME make open fun with custom error parsing?
    suspend fun <T, R> requestTracking(call: suspend () -> Response<T>, transform: (T) -> R): Either<Failure, R> {
        return if (!tokenManager.isRefreshTokenExpired()) {
            try {
                val call = call.invoke()
                val body = call.body()
                when (call.isSuccessful && body != null) {
                    true -> Either.Right(transform(body))
                    false -> {
                        when (call.code()) {
                            in 400..499 -> {
                                Either.Left(RouteTrackingFailure())
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