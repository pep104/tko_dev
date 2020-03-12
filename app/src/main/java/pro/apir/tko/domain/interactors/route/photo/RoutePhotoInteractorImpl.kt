package pro.apir.tko.domain.interactors.route.photo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.model.PhotoModel
import javax.inject.Inject

class RoutePhotoInteractorImpl @Inject constructor(private val routePhotoRepository: RoutePhotoRepository) : RoutePhotoInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun createPhoto(path: String, pointId: Long): PhotoModel = withContext(dispatcher) {
        routePhotoRepository.createPhoto(path, pointId)
    }

    override suspend fun createPhotos(paths: List<String>, pointId: Long): List<PhotoModel> = withContext(dispatcher) {
        val resultList = arrayListOf<PhotoModel>()
        paths.forEach {
            resultList.add(routePhotoRepository.createPhoto(it, pointId))
        }
        resultList
    }

    override suspend fun deletePhoto(photoModel: PhotoModel) = withContext(dispatcher) {
        routePhotoRepository.deletePhoto(photoModel)
    }

    override suspend fun updatePhoto(id: Long, remotePath: String): PhotoModel = withContext(dispatcher) {
        routePhotoRepository.updatePhoto(id, remotePath)
    }

    override suspend fun getPhotos(pointId: Long): List<PhotoModel> = withContext(dispatcher) {
        routePhotoRepository.getPhotos(pointId)
    }

}