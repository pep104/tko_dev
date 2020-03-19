package pro.apir.tko.data.framework.source.route

import pro.apir.tko.data.framework.network.api.RouteActionApi
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 19.03.2020
 * Project: tko-android
 */
class RouteActionSource @Inject constructor(retrofit: Retrofit): RouteActionApi{

    val api by lazy { retrofit.create(RouteActionApi::class.java) }



}