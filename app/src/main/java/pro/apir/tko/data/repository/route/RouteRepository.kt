package pro.apir.tko.data.repository.route

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.RouteModel

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
interface RouteRepository {

    suspend fun getRoutesList(page: Int, pageSize: Int): Either<Failure, List<RouteModel>>

    suspend fun searchRoutes(search: String): Either<Failure, List<RouteModel>>

    suspend fun getRoute(id: Long): Either<Failure, RouteModel>

}