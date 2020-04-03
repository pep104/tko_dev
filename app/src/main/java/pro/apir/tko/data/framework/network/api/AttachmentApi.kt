package pro.apir.tko.data.framework.network.api

import okhttp3.MultipartBody
import pro.apir.tko.data.framework.network.model.response.AttachmentResponse
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */
interface AttachmentApi {

    @Multipart
    @POST("attachments/upload-file/")
    suspend fun uploadFile(@Part files: MultipartBody.Part): Response<List<AttachmentResponse>>

}