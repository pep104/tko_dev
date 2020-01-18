package pro.apir.tko.data.framework.network.model.response

import pro.apir.tko.data.framework.network.model.BaseResponse
import pro.apir.tko.data.framework.network.model.response.data.ContainerData

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
data class ContainersResponse(val count: Int, val next: String?, val previous: String?, val results: List<ContainerData>): BaseResponse()