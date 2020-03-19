package pro.apir.tko.domain.interactors.route.photo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractor
import pro.apir.tko.domain.model.PhotoModel
import pro.apir.tko.domain.model.RouteSessionModel
import javax.inject.Inject

class RoutePhotoInteractorImpl @Inject constructor(private val routePhotoRepository: RoutePhotoRepository, private val routeSessionInteractor: RouteSessionInteractor) : RoutePhotoInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun createPhoto(routeSessionModel: RouteSessionModel, path: String, pointId: Long): RouteSessionModel = withContext(dispatcher) {
        routeSessionInteractor.updateSession(routeSessionModel)
    }

    override suspend fun createPhotos(routeSessionModel: RouteSessionModel, paths: List<String>, pointId: Long): RouteSessionModel = withContext(dispatcher) {
        paths.forEach {
            val id = routePhotoRepository.createPhoto(it, pointId)
            Log.d("photo", "created $id")
        }
        routePhotoRepository.getPhotos(pointId)
        routeSessionInteractor.updateSession(routeSessionModel)
    }

    override suspend fun deletePhoto(routeSessionModel: RouteSessionModel, photoModel: PhotoModel): RouteSessionModel = withContext(dispatcher) {
        routePhotoRepository.deletePhoto(photoModel)
        routeSessionInteractor.updateSession(routeSessionModel)
    }

    override suspend fun updatePhoto(routeSessionModel: RouteSessionModel, id: Long, remotePath: String): RouteSessionModel = withContext(dispatcher) {
        routePhotoRepository.updatePhoto(id, remotePath)
        routeSessionInteractor.updateSession(routeSessionModel)
    }


}