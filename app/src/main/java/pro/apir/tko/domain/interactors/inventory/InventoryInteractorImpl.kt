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
                it.forEach { photo -> uploaded.add(UploadedFileModel(photo.id)) }
            })
        }

        //Combine existing photos and uploaded to result
        val photosCombined = mutableListOf<ImageUploadModel>()
        if (photos != null)
            photosCombined.addAll(photos.map { ImageUploadModel(it.image) })
        photosCombined.addAll(uploaded.map { ImageUploadModel(it.id) })

        //Create edit model
        val edit = ContainerAreaEditModel(
                id = model.id,
                area = model.area,
                containersCount = model.containersCount,
                coordinates = model.coordinates,
                location = model.location,
                registryNumber = model.registryNumber,
                photos = photosCombined,
                hasCover = model.hasCover,
                infoPlate = model.infoPlate,
                access = model.access,
                fence = model.fence,
                coverage = model.coverage,
                kgo = model.kgo
        )

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