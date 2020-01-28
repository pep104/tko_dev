package pro.apir.tko.data.framework.network.model.request


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
)