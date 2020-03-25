package pro.apir.tko.data.framework.network.model.response.routetracking

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.response.routetracking.data.PeriodData
import pro.apir.tko.domain.model.RouteTrackingModel

/**
 * Created by Антон Сарматин
 * Date: 20.03.2020
 * Project: tko-android
 */
data class RouteTrackingResponse(
        @SerializedName("id")
        val sessionId: Long,
        @SerializedName("id")
        val routeId: Long,
        @SerializedName("user")
        val userId: Long,
        val period: PeriodData,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String
) {

    fun toModel() = RouteTrackingModel(sessionId, routeId, userId)

}