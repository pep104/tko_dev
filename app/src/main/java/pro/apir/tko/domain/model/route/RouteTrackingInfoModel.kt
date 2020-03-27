package pro.apir.tko.domain.model.route

/**
 * Created by antonsarmatin
 * Date: 2020-03-25
 * Project: tko-android
 */
data class RouteTrackingInfoModel(val sessionId: Long,
                                  val routeId: Long,
                                  val stopsCompleted: List<RouteTrackingStopModel>,
                                  val createdAt: String)