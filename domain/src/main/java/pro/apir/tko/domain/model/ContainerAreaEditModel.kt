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
        val containers: List<ContainerModel>?,
        val coordinates: CoordinatesModel?,
        val location: String?,
        val registryNumber: String?,
        val photos: List<ImageUploadModel>?,
        val hasCover: Boolean?,
        val infoPlate: Boolean?,
        val access: String?,
        val fence: String?,
        val coverage: String?,
        val kgo: String?
)