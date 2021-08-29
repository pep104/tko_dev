package pro.apir.tko.data.repository.inventory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.onSuccess
import pro.apir.tko.data.cache.ContainerAreaListRuntimeCache
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.framework.network.calladapter.ApiResult
import pro.apir.tko.data.framework.network.model.request.ContainerAreaDetailedRequest
import pro.apir.tko.data.framework.network.model.request.data.ImageRequestData
import pro.apir.tko.data.framework.network.model.response.data.ContainerData
import pro.apir.tko.data.framework.network.model.response.data.CoordinatesData
import pro.apir.tko.data.util.fetchPages
import pro.apir.tko.domain.model.BBoxModel
import pro.apir.tko.domain.model.ContainerAreaEditModel
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.repository.inventory.InventoryRepository
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryApi: InventoryApi,
    private val cache: ContainerAreaListRuntimeCache,
) : InventoryRepository {

    override suspend fun getContainerArea(id: Long): Resource<ContainerAreaShortModel> {
        return inventoryApi.getContainerArea(id).toResult { it.toModel() }
    }

    override suspend fun getContainerAreasByBoundingBox(
        bbox: BBoxModel,
    ): Flow<Resource<List<ContainerAreaListModel>>> = flow {

        fetchPages(
            initialPageRequest = {
                inventoryApi.getContainerAreasByBoundingBox(
                    lngMin = bbox.lngMin.toString(),
                    latMin = bbox.latMin.toString(),
                    lngMax = bbox.lngMax.toString(),
                    latMax = bbox.latMax.toString(),
                    page = 1,
                    pageSize = 50
                )
            },
            pageRequest = { nextPageLink ->
                inventoryApi.getContainerAreasByBoundingBox(nextPageLink)
            },
            processPage = {
                val result = it.toResult { it.results.map { resp -> resp.toModel() } }
                emit(result)
                if (it is ApiResult.Success) {
                    it.data.next
                } else {
                    null
                }
            }
        )

    }

    override suspend fun searchContainerArea(search: String): Resource<List<ContainerAreaListModel>> {
        return inventoryApi.searchContainer(search).toResult {
            it.results.map { res -> res.toModel() }
        }
    }

    override suspend fun updateContainer(model: ContainerAreaEditModel): Resource<ContainerAreaShortModel> {
        val coordinatesModel = model.coordinates
        val coordinatesData = if (coordinatesModel != null) CoordinatesData(coordinatesModel.lng,
            coordinatesModel.lat) else null

        val req = ContainerAreaDetailedRequest(
            model.id,
            model.area,
            coordinatesData,
            model.containers?.map { ContainerData(it.id, it.type, it.loading, it.volume) },
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

        val result = if (model.id == null) {
            inventoryApi.createContainerArea(req)
        } else {
            inventoryApi.updateContainerArea(model.id!!.toLong(), req)
        }.toResult { it.toModel() }




        result.onSuccess {
            cache.remove(it.id.toString())
        }

        return result
    }


}