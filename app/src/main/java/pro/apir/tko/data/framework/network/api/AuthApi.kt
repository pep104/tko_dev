package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.BaseResponse
import pro.apir.tko.data.framework.network.model.response.AuthTokenResponse
import pro.apir.tko.data.framework.network.model.response.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
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
    @POST("/auth/token/")
    suspend fun auth(@Field("email") email: String, @Field("password") password: String) : Response<AuthTokenResponse>

    @FormUrlEncoded
    @POST
    suspend fun refresh(@Field("refresh") refresh: String): Response<RefreshTokenResponse>

}