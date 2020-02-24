package pro.apir.tko.data.repository.route

import android.util.Log
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pro.apir.tko.data.framework.room.dao.RouteSessionDao
import pro.apir.tko.data.framework.room.entity.PointEntity
import pro.apir.tko.data.framework.room.entity.RouteSessionEntity
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants

import javax.inject.Inject

class RouteSessionRepositoryImpl @Inject constructor(private val routeSessionDao: RouteSessionDao) : RouteSessionRepository {

    override suspend fun checkSessionExists(userId: Int, routeId: Int, date: String): Boolean {
        return routeSessionDao.getSession(userId, routeId, date).isNotEmpty()
    }

    override suspend fun createSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel {
        //Insert Session
        val sessionEntity = RouteSessionEntity(null, userId, routeSessionModel.routeId, LocalDate.now().format(DateTimeFormatter.ISO_DATE), false)
        val sessionId = routeSessionDao.insertSession(sessionEntity)
        Log.d("RouteSession","Created session: $sessionId")
        //Insert Points
        val newPoints = mutableListOf<RoutePointModel>()
        routeSessionModel.points.map {
            val pointEntity = PointEntity(null, it.containerId, it.type
                    ?: RouteStateConstants.POINT_TYPE_DEFAULT, sessionId)
            val id = routeSessionDao.insertPoint(pointEntity)
            Log.d("RouteSession","Created session point: $id")
            newPoints.add(RoutePointModel(id, it))
        }

        //Return result
        return RouteSessionModel(sessionId, newPoints, routeSessionModel)
    }

    override suspend fun resumeSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}