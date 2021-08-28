package pro.apir.tko.presentation.utils

import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import pro.apir.tko.domain.model.BBoxModel
import pro.apir.tko.domain.model.LocationModel

/**
 * Created by antonsarmatin
 * Date: 2020-04-07
 * Project: tko-android
 */


fun geoPointFromLocationModel(locationModel: LocationModel): IGeoPoint {
    return GeoPoint(locationModel.lat, locationModel.lon)
}

fun BoundingBox.toModel() = BBoxModel(lonWest, latSouth, lonEast, latNorth)