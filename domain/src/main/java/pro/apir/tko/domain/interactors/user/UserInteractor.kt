package pro.apir.tko.domain.interactors.user

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.UserModel

/**
 * Created by Антон Сарматин
 * Date: 22.02.2020
 * Project: tko-android
 */
interface UserInteractor {

    suspend fun getCurrentUser(): Resource<UserModel>

    suspend fun getCurrentUserId(): Int

}