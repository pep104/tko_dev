package pro.apir.tko.domain.interactors.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface InventoryInteractor {

    suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaListModel>>

    suspend fun getContainerDetailed(id: Long): Either<Failure, ContainerAreaShortModel>

    suspend fun updateContainer(containerAreaShortModel: ContainerAreaShortModel): Either<Failure, ContainerAreaShortModel>

    suspend fun getContainerAreasByBoundingBox(lngMin: String, latMin: String, lngMax: String, latMax: String, page: Int,  pageSize: Int): Either<Failure, List<ContainerAreaListModel>>


}