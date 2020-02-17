package pro.apir.tko.domain.interactors.route

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RouteSessionModel

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
interface RouteSessionInteractor {


    suspend fun getSession(routeModel: RouteModel): Either<Failure, RouteSessionModel>

//    suspend fun startOrResumeSession()



}