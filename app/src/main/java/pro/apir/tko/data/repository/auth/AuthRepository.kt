package pro.apir.tko.data.repository.auth

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.model.RefreshTokenModel

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
interface AuthRepository {

    suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel>

    suspend fun refresh(refresh: String): Either<Failure, RefreshTokenModel>

}