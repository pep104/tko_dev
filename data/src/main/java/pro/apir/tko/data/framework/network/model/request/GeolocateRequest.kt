package pro.apir.tko.data.framework.network.model.request

import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName

/**
 * Created by antonsarmatin
 * Date: 10/05/2021
 * Project: tko
 */
data class GeolocateRequest(
    val lat: Double,
    val lon: Double,
    @SerializedName("radius_meters")
    @IntRange(to= 1_000)
    val radius: Int = 10,
    val count: Int = 1
)