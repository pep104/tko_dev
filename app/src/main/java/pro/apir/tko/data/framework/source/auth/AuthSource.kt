package pro.apir.tko.data.framework.source.auth

import pro.apir.tko.data.framework.network.model.response.AuthTokenResponse
import pro.apir.tko.data.framework.network.model.response.RefreshAccessTokenResponse
import retrofit2.Response

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface AuthSource {


    suspend fun auth(email: String, password: String) : Response<AuthTokenResponse>

    suspend fun refresh(refresh: String): Response<RefreshAccessTokenResponse>

}