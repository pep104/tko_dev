package pro.apir.tko.data.repository.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.domain.model.ContainerAreaModel
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(private val tokenManager: TokenManager, private val inventoryApi: InventoryApi) : InventoryRepository, BaseRepository(tokenManager) {

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaModel>> {
        val result = request({ inventoryApi.getContainerAreas(page, pageSize, location) }, { it.results.map { it.toModel() } })
        return result
    }

    override suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaDetailedModel> {
        val result = request({ inventoryApi.getContainerArea(id) }, { it.toModel() })
        return result
    }

}