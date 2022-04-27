package pro.apir.tko.data.framework.network.model.response.data

import pro.apir.tko.domain.model.map.MapPointModel

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
data class MapPointData(
    val coordinates: CoordinatesData,
    val url: String = "",
) {

    fun toModel() = MapPointModel(coordinates.toModel(), url)

}
