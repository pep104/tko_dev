package pro.apir.tko.data.repository.route.photo

import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.data.mapper.PhotoTypeMapper
import pro.apir.tko.domain.model.PhotoModel
import javax.inject.Inject

class RoutePhotoRepositoryImpl @Inject constructor(private val photoDao: PhotoDao, private val photoTypeMapper: PhotoTypeMapper) : RoutePhotoRepository {

    override suspend fun createPhoto(path: String, pointId: Long): PhotoModel {
        val id = photoDao.insert(PhotoEntity(null, pointId, "local", path))
        return PhotoModel.LocalFile(id, path)
    }

    override suspend fun deletePhoto(photoModel: PhotoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updatePhoto(id: Long, remotePath: String): PhotoModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override suspend fun getPhotos(pointId: Long): List<PhotoModel> {
        val entities = photoDao.selectAllPhotosByPoint(pointId)
        return entities.map { photoTypeMapper.toModel(it) }
    }
}