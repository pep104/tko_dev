package pro.apir.tko.data.framework.source.user

import pro.apir.tko.data.framework.network.api.UserApi
import pro.apir.tko.data.framework.network.model.response.UserInfoResponse
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 22.02.2020
 * Project: tko-android
 */
class UserSource @Inject constructor(val retrofit: Retrofit) : UserApi {

    private val api by lazy { retrofit.create(UserApi::class.java) }

    override suspend fun getUser(): Response<UserInfoResponse> {
        return api.getUser()
    }

}