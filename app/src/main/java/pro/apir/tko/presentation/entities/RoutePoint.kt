package pro.apir.tko.presentation.entities

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */

const val ROUTE_TYPE_DEFAULT = 0
const val ROUTE_TYPE_PENDING = 1
const val ROUTE_TYPE_COMPLETED = 2

data class RoutePoint(var type: Int)