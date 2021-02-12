package pro.apir.tko.domain.model.route

import pro.apir.tko.domain.model.AttachmentModel

data class RouteTrackingStopModel(val id: Long,
                                  val trackId: Long,
                                  val routeStopId: Long,
                                  val attachments: List<AttachmentModel>)
