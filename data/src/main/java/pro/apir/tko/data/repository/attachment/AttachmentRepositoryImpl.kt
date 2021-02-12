package pro.apir.tko.data.repository.attachment

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.source.attachment.IAttachmentSource
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.AttachmentModel
import pro.apir.tko.domain.repository.attachment.AttachmentRepository
import java.io.File
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(credentialsManager: CredentialsManager, private val attachmentApi: IAttachmentSource) : AttachmentRepository, BaseRepository(credentialsManager) {

    override suspend fun uploadFile(file: File): Either<Failure, List<AttachmentModel>> {
        return request({ attachmentApi.uploadFile(file) }, { it.map { item -> item.toModel() } })
    }

}