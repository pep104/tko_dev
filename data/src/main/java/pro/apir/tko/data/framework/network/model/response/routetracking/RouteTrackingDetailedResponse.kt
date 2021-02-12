package pro.apir.tko.data.framework.network.model.response.routetracking

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.response.routetracking.data.PeriodData
import pro.apir.tko.domain.model.route.RouteTrackingInfoModel

/**
 * Created by antonsarmatin
 * Date: 2020-03-26
 * Project: tko-android
 */
data class RouteTrackingDetailedResponse(
        @SerializedName("id")
        val sessionId: Long,
        @SerializedName("route")
        val routeId: Long,
        val period: PeriodData,
        val stops: List<RouteStopTrackingResponse>,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String
) {

    fun toModel() = RouteTrackingInfoModel(sessionId, routeId, stops.map { it.toModel() }, createdAt)

}