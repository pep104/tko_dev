package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.domain.model.ContainerAreaModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
data class ContainerAreaListData(
        val id: Long,
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
        val has_cover: Boolean?,
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
        val platform_basement: String?,
        val registry_number: String?,
        val remoteness: String?,
//        @SerializedName("responsible_person")
//        val responsiblePerson: Any,
        @SerializedName("sources_count")
        val sourcesCount: Int?,
        val status: String?,
        @SerializedName("total_normative")
        val totalNormative: Double?,
        val width: Double?
) {
    fun toModel(): ContainerAreaModel {
        val coordinates = coordinates?.toModel()
        return ContainerAreaModel(id,
                access,
                area,
                closeAt,
                containersCount,
                coordinates,
                coverageType,
                eventsCount,
                fence,
                fullnessPercent,
                has_cover,
                identifier,
                informationPlate,
                lastUpdateAt,
                lastUpdatePerson,
                length,
                location,
                otherBasementDescription,
                ownersNames,
                ownersWasteSourcesNames,
                platform_basement,
                registry_number,
                remoteness,
                sourcesCount,
                status,
                totalNormative,
                width)
    }
}