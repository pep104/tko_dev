package pro.apir.tko.data.repository.attachment

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.api.AttachmentApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.AttachmentModel
import pro.apir.tko.domain.repository.attachment.AttachmentRepository
import java.io.File
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(credentialsManager: CredentialsManager, private val attachmentApi: AttachmentApi) : AttachmentRepository, BaseRepository(credentialsManager) {

    override suspend fun uploadFile(file: File): Either<Failure, List<AttachmentModel>> {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
        return request({ attachmentApi.uploadFile(filePart) }, { it.map { item -> item.toModel() } })
    }

}