package pro.apir.tko.domain.repository.user

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.UserModel

/**
 * Created by Антон Сарматин
 * Date: 22.02.2020
 * Project: tko-android
 */
interface UserRepository {

    suspend fun getUser() : Resource<UserModel>

    suspend fun getUserId() : Int

}