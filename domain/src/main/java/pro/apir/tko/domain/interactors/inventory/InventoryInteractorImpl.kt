package pro.apir.tko.domain.interactors.inventory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.map
import pro.apir.tko.domain.model.*
import pro.apir.tko.domain.repository.attachment.AttachmentRepository
import pro.apir.tko.domain.repository.host.HostRepository
import pro.apir.tko.domain.repository.inventory.InventoryRepository
import pro.apir.tko.domain.utils.substringLocationPrefixRecursively
import java.io.File
import javax.inject.Inject

class InventoryInteractorImpl @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val attachmentRepository: AttachmentRepository,
    private val hostRepository: HostRepository,
) : InventoryInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getContainerArea(id: Long): Resource<ContainerAreaShortModel> {
        return withContext(dispatcher) {
            val host = hostRepository.getHost()
            val data = inventoryRepository.getContainerArea(id)

            return@withContext data.map {
                ContainerAreaShortModel(
                    it.id,
                    it.area,
                    it.containersCount,
                    it.containers,
                    it.coordinates,
                    substringLocationPrefixRecursively(it.location),
                    it.registryNumber,
                    it.photos?.map { imageModel ->
                        imageModel.copy(url = "${host.toUrl()}/${imageModel.url}")
                    },
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


    override suspend fun updateContainer(
        model: ContainerAreaShortModel,
        photos: List<ImageModel>?,
        newPhotos: List<File>?,
    ): Resource<ContainerAreaShortModel> {
        return withContext(dispatcher) {
            val host = hostRepository.getHost()
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
                location = substringLocationPrefixRecursively(model.location),
                registryNumber = regNumber,
                photos = photosCombined,
                hasCover = model.hasCover,
                infoPlate = model.infoPlate,
                access = model.access,
                fence = model.fence,
                coverage = model.coverage,
                kgo = model.kgo
            )

            val result = inventoryRepository.updateContainer(edit)

            return@withContext result.map {
                ContainerAreaShortModel(
                    it.id,
                    it.area,
                    it.containersCount,
                    it.containers,
                    it.coordinates,
                    substringLocationPrefixRecursively(it.location),
                    it.registryNumber,
                    it.photos?.map { imageModel ->
                        imageModel.copy(url = "${host.toUrl()}/${imageModel.url}")
                    },
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

    override suspend fun getContainerAreasByBoundingBox(
        lngMin: Double,
        latMin: Double,
        lngMax: Double,
        latMax: Double,
        page: Int,
        pageSize: Int,
    ): Flow<Resource<List<ContainerAreaListModel>>> = withContext(dispatcher) {
        val result = inventoryRepository.getContainerAreasByBoundingBox(lngMin,
            latMin,
            lngMax,
            latMax,
            page,
            pageSize)
        return@withContext result.map { filterContainerArea(it) }
    }

    override suspend fun searchContainerArea(search: String): Resource<List<ContainerAreaListModel>> {
        return withContext(dispatcher) {
            val result = inventoryRepository.searchContainerArea(search)

            return@withContext filterContainerArea(result)
        }
    }

    private fun filterContainerArea(result: Resource<List<ContainerAreaListModel>>): Resource<List<ContainerAreaListModel>> {
        return if (result is Resource.Success) {
            Resource.Success(
                result.data.filter { it.resourceType == "ContainerWasteArea" }.map {

                    ContainerAreaListModel(
                        it.id,
                        it.identifier,
                        it.registryNumber,
                        substringLocationPrefixRecursively(it.location) ?: "",
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


}