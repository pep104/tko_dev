package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.domain.model.ContainerModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-03
 * Project: tko-android
 */
class ContainerData(
        @SerializedName("container_type")
        val type: String
//todo volume
) {
    fun toModel() = ContainerModel(type)
}