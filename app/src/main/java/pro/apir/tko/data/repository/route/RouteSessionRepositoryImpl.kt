package pro.apir.tko.data.repository.route

import android.util.Log
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import pro.apir.tko.data.framework.network.api.RouteTrackApi
import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.room.dao.PointDao
import pro.apir.tko.data.framework.room.dao.RouteSessionDao
import pro.apir.tko.data.framework.room.entity.PointEntity
import pro.apir.tko.data.framework.room.entity.RouteSessionEntity
import pro.apir.tko.data.framework.room.entity.relation.RouteSessionWithPoints
import pro.apir.tko.data.mapper.PhotoTypeMapper
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants
import javax.inject.Inject

class RouteSessionRepositoryImpl @Inject constructor(private val routeSessionDao: RouteSessionDao,
                                                     private val routePointDao: PointDao,
                                                     private val routePhotoDao: PhotoDao,
                                                     private val photoTypeMapper: PhotoTypeMapper,
                                                     private val routeTrackApi: RouteTrackApi) : RouteSessionRepository {

    //TODO OFFSET FROM BACKEND
    private val offsetHours = 3


    /**
     *
     *  Checks if there is session state record for this User by this day (today from 02:00 to next day 02:00)
     *
     */
    override suspend fun checkSessionExists(userId: Int): Int? {
        val dateRange = getCurrentDateRange(offsetHours)
        val results = routeSessionDao.getExistingSession(userId, dateRange.first, dateRange.second)

        return if (results.isEmpty()) {
            null
        } else {
            val result = results.last()
            if (result.session.isCompleted) {
                null
            } else {
                result.session.routeId
            }
        }
    }

    /**
     *
     *  Checks if there is session state record for this User and Route ID by this day (today from 02:00 to next day 02:00)
     *
     */
    override suspend fun checkSessionExists(userId: Int, routeId: Int): Boolean = checkSessionExists(userId) == routeId

    /**
     *
     *  Creates new state record for RouteSessionModel and starts this RouteSessionModel
     *
     */
    override suspend fun createSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel {
        //Insert Session
        //TODO OFFSET FROM BACKEND
        val sessionEntity = RouteSessionEntity(null, userId, routeSessionModel.routeId, LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(offsetHours)), false)
        val sessionId = routeSessionDao.insertSession(sessionEntity)
        //Insert Points
        val newPoints = mutableListOf<RoutePointModel>()
        routeSessionModel.points.forEach {
            val pointEntity = PointEntity(null, it.containerId, it.type
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
    override suspend fun finishSession(routeSessionModel: RouteSessionModel): RouteSessionModel {
        //TODO network
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
     * Updates this route point with new type and return it
     */
    override suspend fun updatePoint(pointId: Long, type: Int) {
        //TODO COMPLETE POINT AT BACKEND
        //TODO IF SUCCESS THEN UPDATE IT IN DB
        val point = routePointDao.getPoint(pointId)
        val newPoint = PointEntity(point.id, point.containerId, type, point.sessionId)
        routePointDao.updatePoint(newPoint)
    }


    //TODO to mapper class
    private fun mapRouteSessionEntityToModel(routeSessionModel: RouteSessionModel, routeSessionWithPoints: RouteSessionWithPoints): RouteSessionModel {
        val newPoints = mutableListOf<RoutePointModel>()
        routeSessionModel.points.forEach { pointSession ->

            val saved = routeSessionWithPoints.points.find { pointEntity -> pointEntity.point.containerId == pointSession.containerId }
            if (saved != null && saved.point.id != null) {
                val photos = routePhotoDao.selectAllPhotosByPoint(saved.point.id).map { photoTypeMapper.toModel(it) }
                Log.d("photos", "at session interactor: $photos")
                val model = RoutePointModel(saved.point.id, saved.point.type, photos, pointSession)
                Log.d("point","model: $model")
                newPoints.add(model)
            }

        }

        return RouteSessionModel(routeSessionWithPoints.session.id, newPoints, routeSessionModel)
    }


}