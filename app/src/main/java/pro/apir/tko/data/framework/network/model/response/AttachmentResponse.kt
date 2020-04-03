package pro.apir.tko.data.framework.network.model.response

import pro.apir.tko.data.framework.network.model.BaseResponse
import pro.apir.tko.domain.model.AttachmentModel

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */
class AttachmentResponse(
        val id: Int,
        val url: String
) : BaseResponse() {

    fun toModel() = AttachmentModel(id, url)

}