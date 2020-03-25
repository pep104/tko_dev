package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.domain.model.ContainerAreaStopModel

/**
 * Created by Антон Сарматин
 * Date: 08.02.2020
 * Project: tko-android
 */
data class ContainerAreaStopData(
        val id: Int,
        @SerializedName("entity_id")
        val entityId: Int,
        @SerializedName("resourcetype")
        val resourceType: String,
        val location: String?,
        val coordinates: CoordinatesData?,
        @SerializedName("registry_number")
        val registryNumber: String?,
        @SerializedName("containers_count")
        val containersCount: Int?,
        @SerializedName("containers_volume")
        val containersVolume: Double?
) {

    fun toModel() = ContainerAreaStopModel(id, entityId, resourceType, location, coordinates?.toModel(), registryNumber, containersCount, containersVolume)

}