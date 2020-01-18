package pro.apir.tko.domain.interactors.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.domain.model.ContainerModel
import javax.inject.Inject

class InventoryInteractorImpl @Inject constructor(private val inventoryRepository: InventoryRepository) : InventoryInteractor {
    override suspend fun getContainers(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerModel>> {
        return inventoryRepository.getContainers(page, pageSize, location)
    }
}