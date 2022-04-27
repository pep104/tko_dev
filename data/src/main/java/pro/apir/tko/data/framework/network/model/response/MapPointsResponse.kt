package pro.apir.tko.data.framework.network.model.response

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.response.data.MapPointData

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
data class MapPointsResponse(
    @SerializedName("CONTAINER_WASTE_AREA")
    val wasteAreaPoints: List<MapPointData>
)
