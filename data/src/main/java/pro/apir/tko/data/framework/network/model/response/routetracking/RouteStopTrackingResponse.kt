package pro.apir.tko.data.framework.network.model.response.routetracking

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.response.AttachmentResponse
import pro.apir.tko.data.framework.network.model.response.routetracking.data.PeriodData
import pro.apir.tko.domain.model.route.RouteTrackingStopModel

/**
 * Created by Антон Сарматин
 * Date: 20.03.2020
 * Project: tko-android
 */
data class RouteStopTrackingResponse(
        val id: Long,
        @SerializedName("track")
        val trackId: Long,
        @SerializedName("route_stop")
        val stopId: Long,
        val attachments: List<AttachmentResponse>,
        val period: PeriodData,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String) {

    fun toModel() = RouteTrackingStopModel(id, trackId, stopId, attachments.map { it.toModel() })

}
