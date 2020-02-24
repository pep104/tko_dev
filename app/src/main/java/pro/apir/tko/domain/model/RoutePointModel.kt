package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import pro.apir.tko.domain.model.RouteStateConstants.POINT_TYPE_DEFAULT

/**
 * Created by Антон Сарматин
 * Date: 17.02.2020
 * Project: tko-android
 */


@Parcelize
data class RoutePointModel(
        val id: Long?,
        val containerId: Int,
        val resourceType: String,
        val location: String?,
        val coordinates: CoordinatesModel?,
        val registryNumber: String?,
        val containersCount: Int?,
        val containersVolume: Double?,
        var type: Int?
): Parcelable {

    constructor(stop: ContainerAreaStopModel) : this(null,stop.id, stop.resourceType, stop.location, stop.coordinates, stop.registryNumber, stop.containersCount, stop.containersVolume, POINT_TYPE_DEFAULT)

    constructor(id: Long, model: RoutePointModel) : this(id, model.containerId, model.resourceType, model.location, model.coordinates, model.registryNumber, model.containersCount, model.containersVolume, model.type)

}