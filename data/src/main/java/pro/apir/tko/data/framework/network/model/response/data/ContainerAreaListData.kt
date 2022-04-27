package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.domain.model.ContainerAreaListModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
data class ContainerAreaListData(
        val id: Long,
        val identifier: String?,
        @SerializedName("registry_number")
        val registyNumber: String?,
        val location: String?,
        val status: String?,
        val coordinates: CoordinatesData?,
        @SerializedName("containers_count")
        val containersCount: Int,
        val area: Double? = 0.0,
        @SerializedName("resourcetype")
        val resourceType: String?

) {
    fun toModel(): ContainerAreaListModel {
        return ContainerAreaListModel(
                id,
                identifier ?: "",
                registyNumber ?: "",
                location ?: "",
                status ?: "",
                coordinates?.toModel(),
                containersCount,
                area ?: 0.0,
                resourceType ?: "Unknown"
        )
    }
}