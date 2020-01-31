package pro.apir.tko.data.framework.network.model.request


import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import pro.apir.tko.data.framework.network.model.request.data.ImageRequestData
import pro.apir.tko.data.framework.network.model.response.data.CoordinatesData

/**
 * Created by Антон Сарматин
 * Date: 23.01.2020
 * Project: tko-android
 */
data class ContainerAreaDetailedRequest(
        val id: Int?,
        val coordinates: CoordinatesData?,
        val location: String?,
        val registry_number: String?,
        val photos: List<ImageRequestData>?
){

    val status = "ACTIVE"
    val waste_sources = emptyList<Any>()
    @SerializedName("last_update_at")
    val lastUpdateAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

}