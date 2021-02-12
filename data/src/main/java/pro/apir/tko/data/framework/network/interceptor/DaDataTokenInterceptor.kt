package pro.apir.tko.data.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
class DaDataTokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
                .addHeader("Authorization", "Token 3d9d46863f8f9c8df014b4d8a3b554cb71ca1599")
                .build()
        return chain.proceed(request)
    }

}