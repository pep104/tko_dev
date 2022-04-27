package pro.apir.tko.data.framework.network.model.response

import pro.apir.tko.data.framework.network.model.BaseResponse
import pro.apir.tko.data.framework.network.model.response.data.RouteData

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
data class RouteListResponse(val count: Int, val next: String?, val previous: String?, val results: List<RouteData>) : BaseResponse() {

    fun toModelList() = results.map { it.toModel() }

}