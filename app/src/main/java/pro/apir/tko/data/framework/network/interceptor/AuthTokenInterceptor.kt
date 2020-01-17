package pro.apir.tko.data.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import pro.apir.tko.core.constant.KEY_ACCESS_TOKEN
import pro.apir.tko.data.framework.preferences.PreferencesManager
import javax.inject.Inject

class AuthTokenRequestInterceptor @Inject constructor(private val sp: PreferencesManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val v = sp.getString(KEY_ACCESS_TOKEN)
        request = request.newBuilder()
                .addHeader("Authorization", "Bearer $v")
                .build()
        return chain.proceed(request)
    }

}