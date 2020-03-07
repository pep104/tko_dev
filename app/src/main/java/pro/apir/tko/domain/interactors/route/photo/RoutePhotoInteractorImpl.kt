package pro.apir.tko.domain.interactors.route.photo

import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.model.PhotoModel
import javax.inject.Inject

class RoutePhotoInteractorImpl @Inject constructor(private val routePhotoRepository: RoutePhotoRepository) : RoutePhotoInteractor {

    override suspend fun createPhoto(path: String, pointId: Long): PhotoModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deletePhoto(photoModel: PhotoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updatePhoto(id: Long, remotePath: String): PhotoModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPhotos(pointId: Long): List<PhotoModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}