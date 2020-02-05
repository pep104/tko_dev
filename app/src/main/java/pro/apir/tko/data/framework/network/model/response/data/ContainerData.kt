package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.domain.model.ContainerModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-03
 * Project: tko-android
 */
class ContainerData(
        val id: Int?,
        @SerializedName("container_type")
        val type: String,
        val volume: Double?
) {
    fun toModel() = ContainerModel(id, type, volume ?: 0.0)
}