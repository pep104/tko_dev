package pro.apir.tko.domain.interactors.auth

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.model.RefreshTokenModel

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
interface AuthInteractor {

    suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel>

    suspend fun refresh(refresh: String): Either<Failure, RefreshTokenModel>


}