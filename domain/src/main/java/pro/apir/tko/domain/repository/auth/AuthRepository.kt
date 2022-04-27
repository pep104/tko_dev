package pro.apir.tko.domain.repository.auth

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.AuthTokenModel

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
interface AuthRepository {

    suspend fun auth(email: String, password: String): Resource<AuthTokenModel>


}