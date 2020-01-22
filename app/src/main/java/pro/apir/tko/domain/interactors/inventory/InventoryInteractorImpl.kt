package pro.apir.tko.domain.interactors.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.domain.model.ContainerAreaModel
import javax.inject.Inject

class InventoryInteractorImpl @Inject constructor(private val inventoryRepository: InventoryRepository) : InventoryInteractor {

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaModel>> {
        return inventoryRepository.getContainerAreas(page, pageSize, location)
    }

    override suspend fun getContainerDetailed(id: Long): Either<Failure, ContainerAreaDetailedModel> {
        return inventoryRepository.getContainerArea(id)
    }

    override suspend fun updateContainer(containerAreaDetailedModel: ContainerAreaDetailedModel): Either<Failure, ContainerAreaDetailedModel> {
        return inventoryRepository.updateContainer(containerAreaDetailedModel)
    }
}