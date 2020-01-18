package pro.apir.tko.data.repository.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.ContainerModel
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(private val tokenManager: TokenManager, private val inventoryApi: InventoryApi) : InventoryRepository, BaseRepository(tokenManager) {
    override suspend fun getContainers(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerModel>> {
        val result = request(inventoryApi.getContainers(page, pageSize, location)) { it ->
            it.results.map { it.toModel() }
        }
        return result
    }
}