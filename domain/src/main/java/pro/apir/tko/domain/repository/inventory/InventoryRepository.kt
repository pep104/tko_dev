package pro.apir.tko.domain.repository.inventory

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.ContainerAreaEditModel
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface InventoryRepository {

    suspend fun getContainerArea(id: Long): Resource<ContainerAreaShortModel>

    suspend fun getContainerAreasByBoundingBox(lngMin: Double, latMin: Double, lngMax: Double, latMax: Double, page: Int, pageSize: Int): Flow<Resource<List<ContainerAreaListModel>>>

    suspend fun searchContainerArea(search: String): Resource<List<ContainerAreaListModel>>

    suspend fun updateContainer(model: ContainerAreaEditModel): Resource<ContainerAreaShortModel>

}