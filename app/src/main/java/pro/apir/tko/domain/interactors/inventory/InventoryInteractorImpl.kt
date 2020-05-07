package pro.apir.tko.domain.interactors.inventory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.map
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.domain.model.*
import pro.apir.tko.domain.utils.LOCAL_AREA_PREFIXES
import pro.apir.tko.domain.utils.REGION_PREFIXES
import java.io.File
import javax.inject.Inject

class InventoryInteractorImpl @Inject constructor(private val inventoryRepository: InventoryRepository, private val attachmentRepository: AttachmentRepository) : InventoryInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaListModel>> {
        return withContext(dispatcher) {
            val data = inventoryRepository.getContainerAreas(page, pageSize, location)

            return@withContext filterContainerArea(data)

        }
    }

    override suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaShortModel> {
        return withContext(dispatcher) {
            val data = inventoryRepository.getContainerArea(id)

            return@withContext data.map {
                ContainerAreaShortModel(
                        it.id,
                        it.area,
                        it.containersCount,
                        it.containers,
                        it.coordinates,
                        substringLocationPrefix(it.location),
                        it.registryNumber,
                        it.photos,
                        it.hasCover,
                        it.infoPlate,
                        it.access,
                        it.fence,
                        it.coverage,
                        it.kgo
                )
            }
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
                    location = substringLocationPrefix(model.location),
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
            Either.Right(
                    result.b.filter { it.resourceType == "ContainerWasteArea" }.map {

                        ContainerAreaListModel(
                                it.id,
                                it.identifier,
                                it.registryNumber,
                                substringLocationPrefix(it.location) ?: "",
                                it.status,
                                it.coordinates,
                                it.containersCount,
                                it.area,
                                it.resourceType
                        )

                    })
        } else {
            result
        }
    }


    private fun substringLocationPrefix(location: String?): String? {
        val regionMatch = REGION_PREFIXES.filter { location?.contains(it, true) == true }
        val localMatch = LOCAL_AREA_PREFIXES.filter { location?.contains(it, true) == true }

        val substringTimes = when (true) {
            regionMatch.isNotEmpty() && localMatch.isNotEmpty() -> 2
            regionMatch.isNotEmpty() && localMatch.isEmpty() -> 1
            regionMatch.isEmpty() && localMatch.isNotEmpty() -> 1
            else -> 0
        }

        return if (substringTimes != 0) {
            var res: String? = location
            for (index in 0 until substringTimes) {
                res = res?.substringAfter(',')
            }
            return res?.trim()
        } else {
            location
        }

    }
}