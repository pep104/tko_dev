package pro.apir.tko.domain.interactors.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import javax.inject.Inject

class InventoryInteractorImpl @Inject constructor(private val inventoryRepository: InventoryRepository) : InventoryInteractor {

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaListModel>> {
        return inventoryRepository.getContainerAreas(page, pageSize, location)
    }

    override suspend fun getContainerDetailed(id: Long): Either<Failure, ContainerAreaShortModel> {
        return inventoryRepository.getContainerArea(id)
    }

    override suspend fun updateContainer(containerAreaShortModel: ContainerAreaShortModel): Either<Failure, ContainerAreaShortModel> {
        return inventoryRepository.updateContainer(containerAreaShortModel)
    }
}