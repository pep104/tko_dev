package pro.apir.tko.data.repository.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.framework.network.model.request.ContainerAreaDetailedRequest
import pro.apir.tko.data.framework.network.model.response.data.ContainerAreaParametersData
import pro.apir.tko.data.framework.network.model.response.data.CoordinatesData
import pro.apir.tko.data.framework.network.model.response.data.ImageData
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(private val tokenManager: TokenManager, private val inventoryApi: InventoryApi) : InventoryRepository, BaseRepository(tokenManager) {

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaListModel>> {
        val result = request({ inventoryApi.getContainerAreas(page, pageSize, location) }, { it.results.map { it.toModel() } })
        return result
    }

    override suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaShortModel> {
        val result = request({ inventoryApi.getContainerArea(id) }, { it.toModel() })
        return result
    }

    override suspend fun updateContainer(model: ContainerAreaShortModel): Either<Failure, ContainerAreaShortModel> {
        val coordinatesModel = model.coordinates
        val coordinatesData = if (coordinatesModel != null) CoordinatesData(coordinatesModel.lng, coordinatesModel.lat) else null

        val params = model.parameters.map { parametersModel ->
            val photos = parametersModel.photos.map {
                ImageData(it.side, it.image, it.url)
            }
            ContainerAreaParametersData(parametersModel.id, photos)
        }

        val req = ContainerAreaDetailedRequest(model.id, coordinatesData, model.location, model.registryNumber, params)
        return request({
            if (model.id == null) {
                inventoryApi.createContainerArea(req)
            } else {
                inventoryApi.updateContainerArea(model.id.toLong(), req)
            }
        },
                { it.toModel() })
    }


    override suspend fun getContainerAreasByBoundingBox(lngMin: String, latMin: String, lngMax: String, latMax: String, page: Int, pageSize: Int): Either<Failure, List<ContainerAreaListModel>> {
        return request({ inventoryApi.getContainerAreasByBoundingBox(lngMin, latMin, lngMax, latMax, page, pageSize) }, { it.results.map { resp -> resp.toModel() } })
    }
}