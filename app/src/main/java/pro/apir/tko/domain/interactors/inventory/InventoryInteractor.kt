package pro.apir.tko.domain.interactors.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.domain.model.ContainerAreaModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface InventoryInteractor {

    suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaModel>>

    suspend fun getContainerDetailed(id: Long): Either<Failure, ContainerAreaDetailedModel>

    suspend fun updateContainer(containerAreaDetailedModel: ContainerAreaDetailedModel): Either<Failure, ContainerAreaDetailedModel>

}