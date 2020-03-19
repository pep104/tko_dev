package pro.apir.tko.data.repository.route.photo

import android.util.Log
import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.data.mapper.PhotoTypeMapper
import pro.apir.tko.domain.model.PhotoModel
import javax.inject.Inject

class RoutePhotoRepositoryImpl @Inject constructor(private val photoDao: PhotoDao, private val photoTypeMapper: PhotoTypeMapper) : RoutePhotoRepository {

    override suspend fun createPhoto(path: String, pointId: Long): PhotoModel {
        val type = PhotoModel.Type.LOCAL
        val id = photoDao.insert(PhotoEntity(null, pointId, type.name, path))
        return PhotoModel(id, type, path)
    }

    override suspend fun deletePhoto(photoModel: PhotoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updatePhoto(id: Long, remotePath: String): PhotoModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPhotos(pointId: Long) {
        Log.d("photo","${photoDao.selectAllPhotosByPoint(pointId)}")
    }
}