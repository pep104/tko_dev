package pro.apir.tko.data.framework.network.api

import okhttp3.MultipartBody
import pro.apir.tko.data.framework.network.model.response.UploadResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */
interface AttachmentApi {

    @POST("/attachments/upload-file/")
    suspend fun uploadFile(@Part files: MultipartBody.Part): Response<UploadResponse>

}