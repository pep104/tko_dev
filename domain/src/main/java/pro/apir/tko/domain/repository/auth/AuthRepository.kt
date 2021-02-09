package pro.apir.tko.domain.repository.auth

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.AuthTokenModel

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
interface AuthRepository {

    suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel>


}