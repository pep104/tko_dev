package pro.apir.tko.domain.interactors.route.photo

import pro.apir.tko.domain.model.PhotoModel

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
interface RoutePhotoInteractor {

    suspend fun createPhoto(path: String, pointId: Long): PhotoModel

    suspend fun deletePhoto(photoModel: PhotoModel)

    suspend fun updatePhoto(id: Long, remotePath: String): PhotoModel

    suspend fun getPhotos(pointId: Long): List<PhotoModel>

}