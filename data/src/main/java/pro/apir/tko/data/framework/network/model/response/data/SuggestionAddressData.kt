package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName

//ADD PARAMETERS IF NEEDED
data class SuggestionAddressData(
        @SerializedName("geo_lat")
        val lat: Double?,
        @SerializedName("geo_lon")
        val lng: Double?
)