package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
@Parcelize
data class ContainerAreaListModel(
        val id: Long,
        var identifier: String,
        var registryNumber: String,
        var location: String,
        var status: String,
        var coordinates: CoordinatesModel?,
        var containersCount: Int,
        var area: Double = 0.0,
        var resourceType: String
) : Parcelable {

    constructor(shortModel: ContainerAreaShortModel) : this(
            id = shortModel.id?.toLong() ?: 0L,
            identifier = "",
            registryNumber = shortModel.registryNumber ?: "",
            location = shortModel.location ?: "",
            status = "",
            coordinates = shortModel.coordinates,
            containersCount = shortModel.containersCount ?: 0,
            area = shortModel.area ?: 0.0,
            resourceType = ""
    )

    constructor(old: ContainerAreaListModel, shortModel: ContainerAreaShortModel) : this(
            id = shortModel.id?.toLong() ?: 0L,
            identifier = old.identifier,
            registryNumber = shortModel.registryNumber ?: "",
            location = shortModel.location ?: "",
            status = old.status,
            coordinates = shortModel.coordinates,
            containersCount = shortModel.containersCount ?: 0,
            area = shortModel.area ?: 0.0,
            resourceType = old.resourceType
    )

}
