package pro.apir.tko.domain.interactors.route.photo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractor
import pro.apir.tko.domain.model.PhotoModel
import pro.apir.tko.domain.model.RouteSessionModel
import javax.inject.Inject

class RoutePhotoInteractorImpl @Inject constructor(private val routePhotoRepository: RoutePhotoRepository, private val routeSessionInteractor: RouteSessionInteractor) : RoutePhotoInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun createPhotos(sessionModel: RouteSessionModel, photoPaths: List<String>, pointId: Long) = withContext(dispatcher) {

        if (sessionModel.sessionId != null) {
            val createdPhotos = arrayListOf<PhotoModel>()
            photoPaths.forEach {
                val cacheModel = routePhotoRepository.createPhoto(it, sessionModel.sessionId, pointId)
                createdPhotos.add(PhotoModel(cacheModel))
            }
            sessionModel.points.findLast { it.pointId == pointId }?.let { point -> point.photos.addAll(createdPhotos) }
        }
        return@withContext sessionModel
    }

    override suspend fun deletePhoto(sessionModel: RouteSessionModel, photoModel: PhotoModel, pointId: Long): RouteSessionModel = withContext(dispatcher) {
        if (photoModel.id != null)
            routePhotoRepository.deletePhoto(photoModel.id)

        sessionModel.points.findLast { it.pointId == pointId }?.let { point -> point.photos.remove(photoModel) }

        return@withContext sessionModel
    }
}