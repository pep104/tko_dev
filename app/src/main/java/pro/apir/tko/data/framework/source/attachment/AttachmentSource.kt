package pro.apir.tko.data.framework.source.attachment

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pro.apir.tko.data.framework.network.api.AttachmentApi
import pro.apir.tko.data.framework.network.model.response.AttachmentResponse
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject

class AttachmentSource @Inject constructor(retrofit: Retrofit) : IAttachmentSource {

    private val api by lazy { retrofit.create(AttachmentApi::class.java) }

    override suspend fun uploadFile(file: File): Response<List<AttachmentResponse>> {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val filePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
        return api.uploadFile(filePart)
    }
}