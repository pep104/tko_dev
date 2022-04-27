package pro.apir.tko.data.repository.route.photo

import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.domain.model.PhotoCacheModel
import pro.apir.tko.domain.repository.route.photo.RoutePhotoRepository
import java.io.File
import javax.inject.Inject

class RoutePhotoRepositoryImpl @Inject constructor(private val photoDao: PhotoDao) : RoutePhotoRepository {

    override suspend fun createPhoto(path: String, sessionId: Long, pointId: Long): PhotoCacheModel {
        val id = photoDao.insert(PhotoEntity(null, pointId, sessionId, path))
        return PhotoCacheModel(id, pointId, sessionId, path)
    }

    override suspend fun deletePhoto(photoId: Long) {
        val entity = photoDao.get(photoId)
        try {
            File(entity.path).delete()
        } catch (e: Exception) {

        }
        photoDao.delete(entity)
    }

    override suspend fun getPhotosBySession(sessionId: Long): List<PhotoCacheModel> {
        val photos = photoDao.selectAllPhotosBySession(sessionId)
        return photos
                .filter { it.id != null }
                .map {
                    PhotoCacheModel(it.id!!, it.pointId, it.sessionId, it.path)
                }
    }

    override suspend fun getPhotosByPoint(sessionId: Long, pointId: Long): List<PhotoCacheModel> {
        val photos = photoDao.selectAllPhotosByPoint(sessionId, pointId)
        return photos
                .filter { it.id != null }
                .map {
                    PhotoCacheModel(it.id!!, it.pointId, it.sessionId, it.path)
                }
    }
}