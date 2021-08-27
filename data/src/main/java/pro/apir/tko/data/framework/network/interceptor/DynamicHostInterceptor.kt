package pro.apir.tko.data.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import pro.apir.tko.data.framework.manager.host.HostManager
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 27/08/2021
 * Project: tko
 */
class DynamicHostInterceptor @Inject constructor(
    private val hostManager: HostManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val host = hostManager.getHost()
        var request = chain.request()

        if (host.host != request.url.host) {
            val newUrl = request.url.newBuilder().scheme(host.scheme).host(host.host).build()

            request = request.newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(request)
    }

}