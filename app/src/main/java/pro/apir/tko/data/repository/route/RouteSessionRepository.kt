package pro.apir.tko.data.repository.route

import pro.apir.tko.domain.model.RouteSessionModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionRepository {

    //check existing session
    suspend fun checkSessionExists(userId: Int): Int?

    suspend fun checkSessionExists(userId: Int, routeId: Int): Boolean

    //get (start or resume) session
    suspend fun createSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel

    suspend fun resumeSession(userId: Int, routeSessionModel: RouteSessionModel): RouteSessionModel

    suspend fun updatePoint(pointId: Long, type: Int)

}