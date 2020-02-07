package pro.apir.tko.data.repository.route

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.RouteListModel

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
interface RouteRepository {

    suspend fun getRoutesList(page: Int, pageSize: Int): Either<Failure, List<RouteListModel>>

}