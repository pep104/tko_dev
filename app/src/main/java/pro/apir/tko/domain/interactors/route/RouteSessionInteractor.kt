package pro.apir.tko.domain.interactors.route

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionInteractor {

    suspend fun checkSessionExists(routeId: Int): Boolean

//    suspend fun startOrResumeSession()



}