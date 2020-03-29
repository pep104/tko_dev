package pro.apir.tko.domain.interactors.route.photo

import kotlinx.coroutines.Dispatchers
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractor
import javax.inject.Inject

class RoutePhotoInteractorImpl @Inject constructor(private val routePhotoRepository: RoutePhotoRepository, private val routeSessionInteractor: RouteSessionInteractor) : RoutePhotoInteractor {

    private val dispatcher = Dispatchers.IO



}