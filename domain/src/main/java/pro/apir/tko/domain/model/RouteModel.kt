package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
@Parcelize
data class RouteModel(
        val id: Int,
        val name: String,
        val status: String,
        val startLocation: String,
        val startCoordinates: CoordinatesModel,
        val endLocation: String,
        val endCoordinates: CoordinatesModel,
        val stops: List<ContainerAreaStopModel>,
        val path: List<CoordinatesModel>,
        val distance: Int,
        val duration: Int,
        val periodicity: String,
        val hasExistingSession: Boolean = false
) : Parcelable {

    constructor(old: RouteModel, hasExistingSession: Boolean) : this(
            old.id,
            old.name,
            old.status,
            old.startLocation,
            old.startCoordinates,
            old.endLocation,
            old.endCoordinates,
            old.stops,
            old.path,
            old.distance,
            old.duration,
            old.periodicity,
            hasExistingSession
    )


}