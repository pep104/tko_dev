package pro.apir.tko.data.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import pro.apir.tko.data.framework.network.NetworkHandler
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 2019-12-18
 * Project: android-template
 */
class CacheInterceptor @Inject constructor(private val networkHandler: NetworkHandler) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var req = chain.request()
        if (!networkHandler.isConnected) {
            val maxStale = 60 * 60 * 24 * 30
            req = req.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build()
        }
        return chain.proceed(req)
    }

}