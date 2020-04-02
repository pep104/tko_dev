package pro.apir.tko.data.framework.source.attachment

import pro.apir.tko.data.framework.network.model.response.AttachmentResponse
import retrofit2.Response
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */
//Прослойка для преобразования файла в Part
interface IAttachmentSource {

    suspend fun uploadFile(file: File): Response<List<AttachmentResponse>>

}