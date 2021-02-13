package pro.apir.tko.domain.interactors.route

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.RouteModel

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
interface RouteInteractor {

    suspend fun getRoutesList(page: Int, pageSize: Int): Resource<List<RouteModel>>

    suspend fun searchRoutes(search: String): Resource<List<RouteModel>>

}