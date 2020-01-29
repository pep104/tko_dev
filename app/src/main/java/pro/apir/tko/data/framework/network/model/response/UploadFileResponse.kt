package pro.apir.tko.data.framework.network.model.response

import pro.apir.tko.data.framework.network.model.BaseResponse
import pro.apir.tko.domain.model.UploadedFileModel

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */
class UploadFileResponse(val id: Int) : BaseResponse() {

    fun toModel() = UploadedFileModel(id)

}