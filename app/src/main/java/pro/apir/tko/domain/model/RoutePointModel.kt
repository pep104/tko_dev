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
        val pointId: Long,
        val entityId: Int,
        val resourceType: String,
        val location: String?,
        val coordinates: CoordinatesModel?,
        val registryNumber: String?,
        val containersCount: Int?,
        val containersVolume: Double?,
        val distance: Int?,
        val photos: MutableList<PhotoModel>,
        var type: Int?
) : Parcelable {

    constructor(stop: ContainerAreaStopModel) : this(
            stop.id.toLong(),
            stop.entityId,
            stop.resourceType,
            stop.location,
            stop.coordinates,
            stop.registryNumber,
            stop.containersCount,
            stop.containersVolume,
            null,
            mutableListOf(),
            POINT_TYPE_DEFAULT
    )

    constructor(oldModel: RoutePointModel) : this(
            oldModel.pointId,
            oldModel.entityId,
            oldModel.resourceType,
            oldModel.location,
            oldModel.coordinates,
            oldModel.registryNumber,
            oldModel.containersCount,
            oldModel.containersVolume,
            oldModel.distance,
            oldModel.photos,
            oldModel.type
    )

    constructor( type: Int?, photos: List<PhotoModel>, oldModel: RoutePointModel) : this(
            oldModel.pointId,
            oldModel.entityId,
            oldModel.resourceType,
            oldModel.location,
            oldModel.coordinates,
            oldModel.registryNumber,
            oldModel.containersCount,
            oldModel.containersVolume,
            oldModel.distance,
            photos.toMutableList(),
            type
    )

    constructor(distance: Int?, oldModel: RoutePointModel) : this(
            oldModel.pointId,
            oldModel.entityId,
            oldModel.resourceType,
            oldModel.location,
            oldModel.coordinates,
            oldModel.registryNumber,
            oldModel.containersCount,
            oldModel.containersVolume,
            distance,
            oldModel.photos,
            oldModel.type
    )

    constructor(photos: List<PhotoModel>, oldModel: RoutePointModel) : this(
            oldModel.pointId,
            oldModel.entityId,
            oldModel.resourceType,
            oldModel.location,
            oldModel.coordinates,
            oldModel.registryNumber,
            oldModel.containersCount,
            oldModel.containersVolume,
            oldModel.distance,
            photos.toMutableList(),
            oldModel.type
    )


}