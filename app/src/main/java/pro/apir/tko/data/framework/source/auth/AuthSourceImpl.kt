package pro.apir.tko.data.framework.source.auth

import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.network.model.response.AuthTokenResponse
import pro.apir.tko.data.framework.network.model.response.RefreshAccessTokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
class AuthSourceImpl @Inject constructor(@Named("auth") private val retrofit: Retrofit) : AuthApi {

    private val api by lazy { retrofit.create(AuthApi::class.java) }

    override suspend fun auth(email: String, password: String): Response<AuthTokenResponse> {
        return api.auth(email, password)
    }

    override fun refresh(refresh: String): Call<RefreshAccessTokenResponse> {
        return api.refresh(refresh)
    }
}