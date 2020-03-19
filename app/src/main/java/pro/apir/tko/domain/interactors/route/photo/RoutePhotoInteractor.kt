package pro.apir.tko.domain.interactors.route.photo

import pro.apir.tko.domain.model.PhotoModel
import pro.apir.tko.domain.model.RouteSessionModel

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
interface RoutePhotoInteractor {

    suspend fun createPhoto(routeSessionModel: RouteSessionModel,path: String, pointId: Long): RouteSessionModel

    suspend fun createPhotos(routeSessionModel: RouteSessionModel,paths: List<String>, pointId: Long): RouteSessionModel

    suspend fun deletePhoto(routeSessionModel: RouteSessionModel,photoModel: PhotoModel): RouteSessionModel

    suspend fun updatePhoto(routeSessionModel: RouteSessionModel,id: Long, remotePath: String): RouteSessionModel

}