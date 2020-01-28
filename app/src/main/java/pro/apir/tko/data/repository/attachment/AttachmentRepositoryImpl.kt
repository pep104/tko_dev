package pro.apir.tko.data.repository.attachment

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.source.attachment.IAttachmentSource
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.UploadedFileModel
import java.io.File
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(tokenManager: TokenManager, private val attachmentApi: IAttachmentSource) : AttachmentRepository, BaseRepository(tokenManager) {

    override suspend fun uploadFile(file: File): Either<Failure, UploadedFileModel> {
        return request({ attachmentApi.uploadFile(file) }, { it.toModel() })
    }

}