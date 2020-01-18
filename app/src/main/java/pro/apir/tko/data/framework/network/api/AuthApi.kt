package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.response.AuthTokenResponse
import pro.apir.tko.data.framework.network.model.response.RefreshAccessTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
interface AuthApi {

    @FormUrlEncoded
    @POST("auth/token/")
    suspend fun auth(@Field("email") email: String, @Field("password") password: String) : Response<AuthTokenResponse>

    @FormUrlEncoded
    @POST
    fun refresh(@Field("access") refresh: String): Response<RefreshAccessTokenResponse>

}