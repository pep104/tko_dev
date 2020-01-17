package ru.sarmatin.template.data.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.sarmatin.template.data.framework.preferences.SharedPreferences
import javax.inject.Inject

class AuthTokenRequestInterceptor @Inject constructor(private val sp: SharedPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val v = sp.getString("secToken")
        request = request.newBuilder()
                .addHeader("Authorization", "Bearer $v")
                .build()
        return chain.proceed(request)
    }

}