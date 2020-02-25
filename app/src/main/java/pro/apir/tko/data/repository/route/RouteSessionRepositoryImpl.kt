package pro.apir.tko.data.repository.route

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

    /**
     *
     *  Checks if there is session state record for this User and Route by this day (today)
     *
     */
    override suspend fun checkSessionExists(userId: Int, routeId: Int, date: String): Boolean {
        return routeSessionDao.getSession(userId, routeId, date).isNotEmpty()
    }


    /**
     *
     *  Creates new state record for RouteSessionModel and starts this RouteSessionModel
     *
     */
    override suspend fun createSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel {
        //Insert Session
        val sessionEntity = RouteSessionEntity(null, userId, routeSessionModel.routeId, LocalDate.now().format(DateTimeFormatter.ISO_DATE), false)
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

        val savedSessionEntityWithPoints = routeSessionDao.getSession(userId, routeSessionModel.routeId, LocalDate.now().format(DateTimeFormatter.ISO_DATE))

        return if (savedSessionEntityWithPoints.isNotEmpty()) {
            val routeSessionWithPoints = savedSessionEntityWithPoints[0]

            val newPoints = mutableListOf<RoutePointModel>()
            routeSessionModel.points.forEach { pointSession ->
                val saved = routeSessionWithPoints.points.find { pointEntity -> pointEntity.containerId == pointSession.containerId }
                if (saved != null)
                    newPoints.add(RoutePointModel(saved.id, saved.type, pointSession))
            }

            RouteSessionModel(routeSessionWithPoints.session.id, newPoints, routeSessionModel)

        } else {
            createSession(userId, routeSessionModel)
        }

    }
}