package pro.apir.tko.domain.model

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
data class RouteSessionRoomModel(val id: Long, val userId: Int, val routeId: Int, val date: Long, val isCompleted: Boolean, val points: List<PointRoomModel>)