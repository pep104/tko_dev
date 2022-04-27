package pro.apir.tko.data.framework.network.authenticator

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import pro.apir.tko.core.exception.RefreshTokenExpiredException
import pro.apir.tko.data.framework.manager.credentials.CredentialsManager
import pro.apir.tko.data.framework.network.api.AuthApi
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class TokenAuthenticator @Inject constructor(
    private val credentialsManager: CredentialsManager,
    private val authApi: AuthApi,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = credentialsManager.getRefreshToken()
        val ref = authApi.refresh(refreshToken).execute()
        return if (ref.isSuccessful && ref.body() != null) {
            val newToken = ref.body()?.accessRefreshed?.also {
                credentialsManager.saveAccessToken(it)
            }
            ref.body()!!.refresh?.let {
                credentialsManager.saveRefreshToken(it)
            }
            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            Log.e("Authenticator", "Refresh failure")
            throw RefreshTokenExpiredException()
        }
    }

}