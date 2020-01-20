package pro.apir.tko.data.repository.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.domain.model.ContainerAreaModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface InventoryRepository {

    suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaModel>>

    suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaDetailedModel>

}