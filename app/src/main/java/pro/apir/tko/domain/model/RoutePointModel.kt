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
        val pointId: Int,
        val entityId: Int,
        val resourceType: String,
        val location: String?,
        val coordinates: CoordinatesModel?,
        val registryNumber: String?,
        val containersCount: Int?,
        val containersVolume: Double?,
        val distance: Int?,
        val photos: List<PhotoModel>,
        var type: Int?
) : Parcelable {

    constructor(stop: ContainerAreaStopModel) : this(
            null,
            stop.id,
            stop.entityId,
            stop.resourceType,
            stop.location,
            stop.coordinates,
            stop.registryNumber,
            stop.containersCount,
            stop.containersVolume,
            null,
            emptyList(),
            POINT_TYPE_DEFAULT
    )

    constructor(id: Long?, oldModel: RoutePointModel) : this(
            id,
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

    constructor(id: Long?, type: Int?, photos: List<PhotoModel>, oldModel: RoutePointModel) : this(
            id,
            oldModel.pointId,
            oldModel.entityId,
            oldModel.resourceType,
            oldModel.location,
            oldModel.coordinates,
            oldModel.registryNumber,
            oldModel.containersCount,
            oldModel.containersVolume,
            oldModel.distance,
            photos,
            type
    )

    constructor(distance: Int?, oldModel: RoutePointModel) : this(
            oldModel.id,
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
            oldModel.id,
            oldModel.pointId,
            oldModel.entityId,
            oldModel.resourceType,
            oldModel.location,
            oldModel.coordinates,
            oldModel.registryNumber,
            oldModel.containersCount,
            oldModel.containersVolume,
            oldModel.distance,
            photos,
            oldModel.type
    )


}