package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 17.02.2020
 * Project: tko-android
 */
@Parcelize
data class RouteSessionModel(
        val sessionId: Long?,
        var state: Int,
        val routeId: Int,
        val name: String,
        val status: String,
        val startLocation: String,
        val startCoordinates: CoordinatesModel,
        val endLocation: String,
        val endCoordinates: CoordinatesModel,
        val points: List<RoutePointModel>,
        val path: List<CoordinatesModel>,
        val distance: Int,
        val duration: Int,
        val periodicity: String
) : Parcelable {

    constructor(model: RouteModel, state: Int) : this(null, state, model.id, model.name, model.status, model.startLocation, model.startCoordinates, model.endLocation, model.endCoordinates, model.stops.map { RoutePointModel(it) }, model.path, model.distance, model.duration, model.periodicity)

    constructor(sessionId: Long?, points: List<RoutePointModel>, model: RouteSessionModel) :
            this(sessionId, model.state, model.routeId, model.name, model.status, model.startLocation, model.startCoordinates, model.endLocation, model.endCoordinates, points, model.path, model.distance, model.duration, model.periodicity)

}