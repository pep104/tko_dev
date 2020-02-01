package pro.apir.tko.data.framework.network.model.response

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.response.data.CoordinatesData
import pro.apir.tko.data.framework.network.model.response.data.ImageData
import pro.apir.tko.domain.model.ContainerAreaShortModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
data class ContainerAreaResponse(
        val id: Int,
        val access: String?,
        val area: Double?,
        @SerializedName("close_at")
        val closeAt: String?,
        @SerializedName("containers_count")
        val containersCount: Int?,
        val coordinates: CoordinatesData?,
        @SerializedName("coverage_type")
        val coverageType: String?,
        @SerializedName("events_count")
        val eventsCount: Int?,
        val fence: String?,
        @SerializedName("fullness_percent")
        val fullnessPercent: String?,
        @SerializedName("has_cover")
        val hasCover: Boolean?,
        val identifier: String?,
        @SerializedName("information_plate")
        val informationPlate: Boolean?,
        @SerializedName("last_update_at")
        val lastUpdateAt: String?,
        @SerializedName("last_update_person")
        val lastUpdatePerson: String?,
        val length: Double?,
        val location: String?,
        @SerializedName("other_basement_description")
        val otherBasementDescription: String?,
        @SerializedName("owners_names")
        val ownersNames: String?,
        @SerializedName("owners_waste_sources_names")
        val ownersWasteSourcesNames: String?,
        @SerializedName("platform_basement")
        val basement: String?,
        @SerializedName("registry_number")
        val registryNumber: String?,
        val remoteness: String?,
//        @SerializedName("responsible_person")
//        val responsiblePerson: Any,
        @SerializedName("section_for_kgo")
        val kgo: String?,
        @SerializedName("sources_count")
        val sourcesCount: Int?,
        val status: String?,
        @SerializedName("total_normative")
        val totalNormative: Double?,
        val width: Double?,
        val photos: List<ImageData>?
) {
    //TODO EXTRACT MAPPERS FROM MODEL TO MAPPER CLASS
    fun toModel(): ContainerAreaShortModel {
        val coordinates = coordinates?.toModel()
        return ContainerAreaShortModel(
                id = id,
                area = area,
                containersCount = containersCount,
                coordinates = coordinates,
                location = location,
                registryNumber = registryNumber,
                photos = photos?.map { it.toModel() },
                hasCover = hasCover,
                infoPlate = informationPlate,
                access = access,
                fence = fence,
                coverage = coverageType,
                kgo = kgo

        )
    }
}