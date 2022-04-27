package pro.apir.tko.data.framework.network.model.request

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.request.data.LocationRequestData

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
data class SuggestionRequest(
        val query: String,
        @SerializedName("locations_geo")
        val location: List<LocationRequestData>? = null,
        val count: Int? = null
)