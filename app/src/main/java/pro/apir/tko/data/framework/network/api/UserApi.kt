package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.response.UserInfoResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by Антон Сарматин
 * Date: 22.02.2020
 * Project: tko-android
 */
interface UserApi {

    @GET("users/me/")
    suspend fun getUser(): Response<UserInfoResponse>

}