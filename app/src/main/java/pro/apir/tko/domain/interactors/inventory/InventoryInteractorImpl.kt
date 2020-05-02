package pro.apir.tko.domain.interactors.inventory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.map
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.domain.model.*
import pro.apir.tko.domain.utils.REGION_PREFIXES
import java.io.File
import javax.inject.Inject

class InventoryInteractorImpl @Inject constructor(private val inventoryRepository: InventoryRepository, private val attachmentRepository: AttachmentRepository) : InventoryInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaListModel>> {
        return withContext(dispatcher) {
            val data = inventoryRepository.getContainerAreas(page, pageSize, location)

            return@withContext data.map {
                it.map {
                    if (REGION_PREFIXES.contains(it.location)) {
                        ContainerAreaListModel(
                                it.id,
                                it.identifier,
                                it.registryNumber,
                                it.location.substringAfter(","),
                                it.status,
                                it.coordinates,
                                it.containersCount,
                                it.area,
                                it.resourceType
                        )
                    } else {
                        it
                    }
                }
            }

        }
    }

    override suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaShortModel> {
        return withContext(dispatcher) {
            val data = inventoryRepository.getContainerArea(id)

            TODO()
        }
    }


    override suspend fun updateContainer(model: ContainerAreaShortModel, photos: List<ImageModel>?, newPhotos: List<File>?): Either<Failure, ContainerAreaShortModel> {
        return withContext(dispatcher) {
            //Upload new photos
            val uploaded = mutableListOf<AttachmentModel>()
            newPhotos?.forEach { newPhoto ->
                attachmentRepository.uploadFile(newPhoto).fold({}, {
                    uploaded.addAll(it)
                })
            }

            //Combine existing photos and uploaded to result
            val photosCombined = mutableListOf<ImageUploadModel>()
            if (photos != null)
                photosCombined.addAll(photos.map { ImageUploadModel(it.image) })
            photosCombined.addAll(uploaded.map { ImageUploadModel(it.id) })


            //FIXME исправить эту дичь с кучей моделей
            val regNumber = if (model.registryNumber.isNullOrBlank()) null else model.registryNumber

            //Create edit model
            val edit = ContainerAreaEditModel(
                    id = model.id,
                    area = model.area,
                    containersCount = model.containersCount,
                    containers = model.containers,
                    coordinates = model.coordinates,
                    location = model.location,
                    registryNumber = regNumber,
                    photos = photosCombined,
                    hasCover = model.hasCover,
                    infoPlate = model.infoPlate,
                    access = model.access,
                    fence = model.fence,
                    coverage = model.coverage,
                    kgo = model.kgo
            )

            inventoryRepository.updateContainer(edit)
        }
    }

    override suspend fun getContainerAreasByBoundingBox(lngMin: Double, latMin: Double, lngMax: Double, latMax: Double, page: Int, pageSize: Int): Either<Failure, List<ContainerAreaListModel>> {
        return withContext(dispatcher) {
            val result = inventoryRepository.getContainerAreasByBoundingBox(lngMin, latMin, lngMax, latMax, page, pageSize)

            return@withContext filterContainerArea(result)
        }
    }

    override suspend fun searchContainerArea(search: String): Either<Failure, List<ContainerAreaListModel>> {
        return withContext(dispatcher) {
            val result = inventoryRepository.searchContainerArea(search)

            return@withContext filterContainerArea(result)
        }
    }

    private fun filterContainerArea(result: Either<Failure, List<ContainerAreaListModel>>): Either<Failure, List<ContainerAreaListModel>> {
        return if (result is Either.Right) {
            Either.Right(result.b.filter { it.resourceType == "ContainerWasteArea" })
        } else {
            result
        }
    }
}