package pro.apir.tko.domain.interactors.inventory

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.model.ImageModel
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface InventoryInteractor {

    suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Either<Failure, List<ContainerAreaListModel>>

    suspend fun getContainerArea(id: Long): Either<Failure, ContainerAreaShortModel>

    suspend fun updateContainer(model: ContainerAreaShortModel, photos: List<ImageModel>?, newPhotos: List<File>?): Either<Failure, ContainerAreaShortModel>

    suspend fun getContainerAreasByBoundingBox(lngMin: Double, latMin: Double, lngMax: Double, latMax: Double, page: Int,  pageSize: Int): Either<Failure, List<ContainerAreaListModel>>

    suspend fun searchContainerArea(search: String): Either<Failure, List<ContainerAreaListModel>>

}