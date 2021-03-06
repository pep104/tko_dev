package pro.apir.tko.data.framework.network.model.request


import com.google.gson.annotations.SerializedName
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import pro.apir.tko.data.framework.network.model.request.data.ImageRequestData
import pro.apir.tko.data.framework.network.model.response.data.ContainerData
import pro.apir.tko.data.framework.network.model.response.data.CoordinatesData
import java.util.*

/**
 * Created by Антон Сарматин
 * Date: 23.01.2020
 * Project: tko-android
 */
data class ContainerAreaDetailedRequest(
        val id: Int?,
        val area: Double?,
        val coordinates: CoordinatesData?,
        val containers: List<ContainerData>?,
        val location: String?,
        @SerializedName("registry_number")
        val registryNumber: String?,
        val photos: List<ImageRequestData>?,
        @SerializedName("has_cover")
        val hasCover : Boolean?,
        @SerializedName("information_plate")
        val infoPlate: Boolean?,
        val access: String?,
        val fence: String?,
        @SerializedName("coverage_type")
        val coverage: String?,
        @SerializedName("section_for_kgo")
        val kgo: String?

) {

    //Поля, которые должны быть при создании новой записи, но при редактировании нет
    val status = if (id == null) "ACTIVE" else null
    val waste_sources = if (id == null) emptyList<Any>() else null

    //Особенность API - посылаем время изменения на сервер всегда.
    @SerializedName("last_update_at")
    val lastUpdateAt: String = OffsetDateTime.now(ZoneId.of(TimeZone.getDefault().id)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

}