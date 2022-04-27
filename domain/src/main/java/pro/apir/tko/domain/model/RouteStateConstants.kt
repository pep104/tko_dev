package pro.apir.tko.domain.model

/**
 * Created by Антон Сарматин
 * Date: 17.02.2020
 * Project: tko-android
 */
object RouteStateConstants {

    const val POINT_TYPE_DEFAULT = 0
    const val POINT_TYPE_PENDING = 1
    const val POINT_TYPE_COMPLETED = 2

    /**
     * Default (Initial) state of RouteSessionModel
     * This RouteSessionModel in default state and can be started from scratch
     */
    const val ROUTE_TYPE_DEFAULT = 0

    /**
     * Pending state of RouteSessionModel
     * This RouteSessionModel has saved progress and can be resumed
     */
    const val ROUTE_TYPE_PENDING = 1

    /**
     * RouteSessionModel already in progress
     */
    const val ROUTE_TYPE_IN_PROGRESS = 2

    /**
     * RouteSessionModel completed
     * This route completed
     */
    const val ROUTE_TYPE_COMPLETED = 3

    /**
     * Other RouteSession is started but not completed yet
     * New route not able to start
     */
    const val ROUTE_TYPE_START_DISABLED = 4


}