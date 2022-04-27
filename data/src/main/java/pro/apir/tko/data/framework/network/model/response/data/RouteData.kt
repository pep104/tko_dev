package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.domain.model.RouteModel

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
data class RouteData(val id: Int,
                     val name: String,
                     val status: String,
                     @SerializedName("start_location")
                     val startLocation: String,
                     @SerializedName("start_coordinates")
                     val startCoordinates: CoordinatesData,
                     @SerializedName("end_location")
                     val endLocation: String,
                     @SerializedName("end_coordinates")
                     val endCoordinates: CoordinatesData,
                     val stops: List<ContainerAreaStopData>,
                     @SerializedName("path_polyline")
                     val path: List<CoordinatesData>,
                     @SerializedName("estimated_distance")
                     val distance: Int,
                     @SerializedName("estimated_duration")
                     val duration: Int,
                     val periodicity: String
) {

    fun toModel() = RouteModel(
            id,
            name,
            status,
            startLocation,
            startCoordinates.toModel(),
            endLocation,
            endCoordinates.toModel(),
            stops.map { it.toModel() }.filter { it.location != null },
            path.map { it.toModel() },
            distance,
            duration,
            periodicity
    )

    fun toModel(hasExistingRouteSession: Boolean) = RouteModel(
            id,
            name,
            status,
            startLocation,
            startCoordinates.toModel(),
            endLocation,
            endCoordinates.toModel(),
            stops.map { it.toModel() }.filter { it.location != null },
            path.map { it.toModel() },
            distance,
            duration,
            periodicity,
            hasExistingRouteSession
    )

}