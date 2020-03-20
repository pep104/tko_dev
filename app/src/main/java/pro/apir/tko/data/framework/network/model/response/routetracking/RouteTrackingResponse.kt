package pro.apir.tko.data.framework.network.model.response.routetracking

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.response.routetracking.data.PeriodData

/**
 * Created by Антон Сарматин
 * Date: 20.03.2020
 * Project: tko-android
 */
data class RouteTrackingResponse(
        val id: Long,
        val route: Long,
        val user: Long,
        val period: PeriodData,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String
)