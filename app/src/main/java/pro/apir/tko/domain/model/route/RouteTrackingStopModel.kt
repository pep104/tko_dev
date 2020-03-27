package pro.apir.tko.domain.model.route

data class RouteTrackingStopModel(val id: Long,
                                  val track: Long,
                                  val stop: Long,
                                  val attachments: List<String>)
