package pro.apir.tko.data.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import pro.apir.tko.core.exception.RefreshTokenNotValidException
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import javax.inject.Inject

class TokenInterceptor @Inject constructor(private val credentialsManager: CredentialsManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        if (credentialsManager.isRefreshTokenExpired()) {
            // IOException будет обработана в onFailure retrofit'a
            throw RefreshTokenNotValidException()
        }

        var request = chain.request()
        val token = credentialsManager.getAccessToken()
        request = request.newBuilder()
                .addHeader("Authorization", "Bearer ")
                .build()
        return chain.proceed(request)
    }

}