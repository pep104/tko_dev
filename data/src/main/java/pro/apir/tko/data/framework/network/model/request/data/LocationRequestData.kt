package pro.apir.tko.data.framework.network.model.request.data

import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName

/**
 * Created by antonsarmatin
 * Date: 03/05/2021
 * Project: tko
 */
data class LocationRequestData(
        val lat: Double,
        val lon: Double,
        @SerializedName("radius_meters")
        @IntRange(to= 100_000)
        val radius: Int = 10_000
)
