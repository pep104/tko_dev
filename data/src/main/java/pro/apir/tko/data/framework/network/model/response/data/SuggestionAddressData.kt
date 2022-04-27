package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName

//ADD PARAMETERS IF NEEDED
data class SuggestionAddressData(
        @SerializedName("geo_lat")
        val lat: Double?,
        @SerializedName("geo_lon")
        val lng: Double?,

        @SerializedName("city_type")
        val cityType: String?,
        @SerializedName("city_type_full")
        val cityTypeFull: String?,
        val city: String?,
        val cityFull:String?,

        @SerializedName("street_type")
        val streetType: String?,
        @SerializedName("street_type_full")
        val streetTypeFull: String?,
        val street: String?,
        val streetFull:String?,

        @SerializedName("house_type")
        val houseType: String?,
        @SerializedName("house_type_full")
        val houseTypeFull: String?,
        val house: String?,
        val houseFull:String?,

        @SerializedName("fias_level")
        val fiasLevel: Int
)