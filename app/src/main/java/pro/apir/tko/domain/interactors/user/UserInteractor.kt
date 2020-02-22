package pro.apir.tko.domain.interactors.user

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.UserModel

/**
 * Created by Антон Сарматин
 * Date: 22.02.2020
 * Project: tko-android
 */
interface UserInteractor {

    suspend fun getCurrentUser(): Either<Failure, UserModel>

}