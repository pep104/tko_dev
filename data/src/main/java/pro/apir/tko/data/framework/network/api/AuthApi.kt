package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.calladapter.ApiResult
import pro.apir.tko.data.framework.network.model.response.AuthTokenResponse
import pro.apir.tko.data.framework.network.model.response.RefreshAccessTokenResponse
import retrofit2.Call
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
    suspend fun auth(@Field("email") email: String, @Field("password") password: String): ApiResult<AuthTokenResponse>

    //Extract to RefreshApi?
    @FormUrlEncoded
    @POST("auth/token/refresh/")
    fun refresh(@Field("refresh") refresh: String): Call<RefreshAccessTokenResponse>

}