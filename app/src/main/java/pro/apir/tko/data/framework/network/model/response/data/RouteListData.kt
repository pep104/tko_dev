package pro.apir.tko.data.framework.network.model.response.data

import pro.apir.tko.domain.model.RouteListModel

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
//TODO
data class RouteListData(val id: Int){

    fun toModel() = RouteListModel(id)

}