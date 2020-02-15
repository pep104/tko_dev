package pro.apir.tko.data.repository.route

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionRepository {

    //check existing session
    suspend fun checkSessionExists(userId: Int, routeId: Int, date: String): Boolean

    //get (start or resume) session

    //update point

}