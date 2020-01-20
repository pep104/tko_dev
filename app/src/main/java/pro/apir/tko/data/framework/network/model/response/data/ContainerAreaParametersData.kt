package pro.apir.tko.data.framework.network.model.response.data

import pro.apir.tko.domain.model.ContainerAreaParametersModel


/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
data class ContainerAreaParametersData(
        val id: Long,
        val photos: List<ImageData>
) {
    fun toModel() = ContainerAreaParametersModel(id, photos.map { it.toModel() })
}