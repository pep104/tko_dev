package pro.apir.tko.domain.model.route

import pro.apir.tko.domain.model.PhotoCacheModel

/**
 * Created by antonsarmatin
 * Date: 2020-03-25
 * Project: tko-android
 */
data class RouteTrackingInfoModel(val sessionId: Long,
                                  val routeId: Long,
                                  val stopsCompleted: List<RouteTrackingStopModel>,
                                  val createdAt: String,
                                  val localPhotos: MutableList<PhotoCacheModel> = arrayListOf(),
                                  val isCompleted: Boolean = false)