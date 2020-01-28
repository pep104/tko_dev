package pro.apir.tko.data.framework.network.model.response.data

import pro.apir.tko.domain.model.CoordinatesModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class CoordinatesData(val lng: Double, val lat: Double) {
    //TODO EXTRACT MAPPERS FROM MODEL TO MAPPER CLASS
    fun toModel() = CoordinatesModel(lng, lat)
}