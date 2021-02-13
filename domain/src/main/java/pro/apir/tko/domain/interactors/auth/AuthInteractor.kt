package pro.apir.tko.domain.interactors.auth

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.AuthTokenModel

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
interface AuthInteractor {

    suspend fun auth(email: String, password: String): Resource<AuthTokenModel>

}