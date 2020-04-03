package pro.apir.tko.domain.interactors.route.photo

import pro.apir.tko.domain.model.PhotoModel
import pro.apir.tko.domain.model.RouteSessionModel

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
interface RoutePhotoInteractor {

    suspend fun createPhotos(sessionModel: RouteSessionModel, photoPaths: List<String>, pointId: Long): RouteSessionModel

    suspend fun deletePhoto(sessionModel: RouteSessionModel, photoModel: PhotoModel, pointId: Long): RouteSessionModel

}