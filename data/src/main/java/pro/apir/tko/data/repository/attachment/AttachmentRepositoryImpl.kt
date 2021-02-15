package pro.apir.tko.data.repository.attachment

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import pro.apir.tko.core.data.Resource
import pro.apir.tko.data.framework.network.api.AttachmentApi
import pro.apir.tko.domain.model.AttachmentModel
import pro.apir.tko.domain.repository.attachment.AttachmentRepository
import java.io.File
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(private val attachmentApi: AttachmentApi) : AttachmentRepository {

    override suspend fun uploadFile(file: File): Resource<List<AttachmentModel>> {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
        return attachmentApi.uploadFile(filePart).toResult { it.map { item -> item.toModel() } }
    }
}

