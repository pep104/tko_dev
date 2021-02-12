package pro.apir.tko.domain.repository.route.photo

import pro.apir.tko.domain.model.PhotoCacheModel

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
interface RoutePhotoRepository {

    suspend fun getPhotosBySession(sessionId: Long): List<PhotoCacheModel>

    suspend fun getPhotosByPoint(sessionId: Long, pointId: Long): List<PhotoCacheModel>

    suspend fun createPhoto(path: String, sessionId: Long, pointId: Long): PhotoCacheModel

    suspend fun deletePhoto(photoId: Long)


}