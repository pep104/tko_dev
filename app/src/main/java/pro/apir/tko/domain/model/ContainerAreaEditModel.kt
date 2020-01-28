package pro.apir.tko.domain.model

/**
 * Created by Антон Сарматин
 * Date: 29.01.2020
 * Project: tko-android
 */
data class ContainerAreaEditModel(
        val id: Int?,
        val area: Double?,
        val containersCount: Int?,
        var coordinates: CoordinatesModel?,
        var location: String?,
        var registryNumber: String?,
        var photos: List<ImageUploadModel>?
)