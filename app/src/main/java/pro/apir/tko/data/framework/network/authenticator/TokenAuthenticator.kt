package pro.apir.tko.data.framework.network.authenticator

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.AuthApi
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class TokenAuthenticator @Inject constructor(private val tokenManager: TokenManager, private val authApi: AuthApi) : Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? {
        Log.e("authenticator", "authenticate runs")
        val refreshToken = tokenManager.getRefreshToken()
        val ref = authApi.refresh(refreshToken).execute()
        return if (ref.isSuccessful && ref.body() != null) {
            val newToken = ref.body()!!.accessRefreshed
            Log.e("authenticator", "new token: $newToken")
            tokenManager.saveAccessToken(newToken)
            response.request.newBuilder().header("Authorization", "Bearer $newToken").build()
        } else {
            Log.e("authenticator", "refresh failure")
            null
        }
    }
}