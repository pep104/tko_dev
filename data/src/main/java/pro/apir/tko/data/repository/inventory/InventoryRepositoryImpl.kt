package pro.apir.tko.data.repository.inventory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.onSuccess
import pro.apir.tko.data.cache.ContainerAreaListCache
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.framework.network.model.request.ContainerAreaDetailedRequest
import pro.apir.tko.data.framework.network.model.request.data.ImageRequestData
import pro.apir.tko.data.framework.network.model.response.data.ContainerData
import pro.apir.tko.data.framework.network.model.response.data.CoordinatesData
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.ContainerAreaEditModel
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.repository.inventory.InventoryRepository
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
        private val credentialsManager: CredentialsManager,
        private val inventoryApi: InventoryApi,
        private val cache: ContainerAreaListCache
) : InventoryRepository, BaseRepository(credentialsManager) {

    override suspend fun getContainerArea(id: Long): Resource<ContainerAreaShortModel> {
        val result = request({ inventoryApi.getContainerArea(id) }, { it.toModel() })
        return result
    }

    override suspend fun getContainerAreasByBoundingBox(
            lngMin: Double,
            latMin: Double,
            lngMax: Double,
            latMax: Double,
            page: Int,
            pageSize: Int
    ): Flow<Resource<List<ContainerAreaListModel>>> = flow {
        val cached = cache.getAll()

        if (cached != null) emit(Resource.Success(cached))

        val result = request({ inventoryApi.getContainerAreasByBoundingBox(lngMin.toString(), latMin.toString(), lngMax.toString(), latMax.toString(), page, pageSize) }, { it.results.map { resp -> resp.toModel() } })
        emit(result)
        result.onSuccess {
            it.forEach {
                cache.put(it.id.toString(), it)
            }
        }
    }

    override suspend fun searchContainerArea(search: String): Resource<List<ContainerAreaListModel>> {
        return request(
                call = {
                    inventoryApi.searchContainer(search)
                },
                transform = {
                    it.results.map { res -> res.toModel() }
                })
    }

    override suspend fun updateContainer(model: ContainerAreaEditModel): Resource<ContainerAreaShortModel> {
        val coordinatesModel = model.coordinates
        val coordinatesData = if (coordinatesModel != null) CoordinatesData(coordinatesModel.lng, coordinatesModel.lat) else null

        val req = ContainerAreaDetailedRequest(
                model.id,
                model.area,
                coordinatesData,
                model.containers?.map { ContainerData(it.id, it.type, it.volume) },
                model.location,
                model.registryNumber,
                model.photos?.map { ImageRequestData(it.image) },
                model.hasCover,
                model.infoPlate,
                model.access,
                model.fence,
                model.coverage,
                model.kgo
        )

        val result = request(
                {
                    if (model.id == null) {
                        inventoryApi.createContainerArea(req)
                    } else {
                        inventoryApi.updateContainerArea(model.id!!.toLong(), req)
                    }
                },
                { it.toModel() }
        )

        result.onSuccess {
            cache.remove(it.id.toString())
        }

        return result
    }


}