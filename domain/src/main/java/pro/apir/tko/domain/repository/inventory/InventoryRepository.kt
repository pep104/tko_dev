package pro.apir.tko.domain.repository.inventory

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.ContainerAreaEditModel
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface InventoryRepository {

    suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaShortModel>

    suspend fun getContainerAreasByBoundingBox(lngMin: Double, latMin: Double, lngMax: Double, latMax: Double, page: Int, pageSize: Int): Flow<Either<Failure, List<ContainerAreaListModel>>>

    suspend fun searchContainerArea(search: String): Either<Failure, List<ContainerAreaListModel>>

    suspend fun updateContainer(model: ContainerAreaEditModel): Either<Failure, ContainerAreaShortModel>

}