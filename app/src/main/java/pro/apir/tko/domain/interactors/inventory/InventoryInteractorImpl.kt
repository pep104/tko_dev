package pro.apir.tko.domain.interactors.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.domain.model.*
import java.io.File
import javax.inject.Inject

class InventoryInteractorImpl @Inject constructor(private val inventoryRepository: InventoryRepository, private val attachmentRepository: AttachmentRepository) : InventoryInteractor {

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaListModel>> {
        return inventoryRepository.getContainerAreas(page, pageSize, location)
    }

    override suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaShortModel> {
        return inventoryRepository.getContainerArea(id)
    }


    override suspend fun updateContainer(model: ContainerAreaShortModel, photos: List<ImageModel>?, newPhotos: List<File>?): Either<Failure, ContainerAreaShortModel> {

        //Upload new photos
        val uploaded = mutableListOf<UploadedFileModel>()
        newPhotos?.forEach { newPhoto ->
            attachmentRepository.uploadFile(newPhoto).fold({}, {
                uploaded.add(it)
            })
        }
        //Combine existing photos and uploaded to result
        val result = mutableListOf<ImageUploadModel>()
        if (photos != null)
            result.addAll(photos.map { ImageUploadModel(it.image) })
        result.addAll(uploaded.map { ImageUploadModel(0) })

        //todo new photos to cont
        val edit = ContainerAreaEditModel(model.id, model.area, model.containersCount, model.coordinates, model.location, model.registryNumber, result)
        return inventoryRepository.updateContainer(edit)
    }

    override suspend fun getContainerAreasByBoundingBox(lngMin: Double, latMin: Double, lngMax: Double, latMax: Double, page: Int, pageSize: Int): Either<Failure, List<ContainerAreaListModel>> {
        val result = inventoryRepository.getContainerAreasByBoundingBox(lngMin, latMin, lngMax, latMax, page, pageSize)

        return if (result is Either.Right) {
            Either.Right(result.b.filter { it.resourceType == "ContainerWasteArea" })
        } else {
            result
        }
    }
}