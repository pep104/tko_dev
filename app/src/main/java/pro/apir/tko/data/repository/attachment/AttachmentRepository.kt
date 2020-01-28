package pro.apir.tko.data.repository.attachment

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.UploadedFileModel
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */

interface AttachmentRepository {

    suspend fun uploadFile(file: File): Either<Failure, UploadedFileModel>


}