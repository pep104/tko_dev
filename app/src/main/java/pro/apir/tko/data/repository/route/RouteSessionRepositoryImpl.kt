package pro.apir.tko.data.repository.route

import pro.apir.tko.data.framework.room.dao.RouteSessionDao
import javax.inject.Inject

class RouteSessionRepositoryImpl @Inject constructor(private val routeSessionDao: RouteSessionDao) : RouteSessionRepository{

   override suspend fun checkSessionExists(userId: Int, routeId: Int, date: String): Boolean {
        return routeSessionDao.getSession(userId, routeId, date).isNotEmpty()
    }

}