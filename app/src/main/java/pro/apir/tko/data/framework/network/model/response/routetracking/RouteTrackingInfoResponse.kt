package pro.apir.tko.data.framework.network.model.response.routetracking

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.response.routetracking.data.PeriodData

/**
 * Created by Антон Сарматин
 * Date: 20.03.2020
 * Project: tko-android
 */
data class RouteTrackingInfoResponse(
        @SerializedName("id")
        val sessionId: Long,
        @SerializedName("route")
        val routeId: Long,
        @SerializedName("user")
        val userId: Long,
        val period: PeriodData,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String
)