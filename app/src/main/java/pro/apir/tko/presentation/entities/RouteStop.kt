package pro.apir.tko.presentation.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import pro.apir.tko.domain.model.ContainerAreaStopModel
import pro.apir.tko.domain.model.RouteStateConstants

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */

@Parcelize
data class RouteStop(val stop: ContainerAreaStopModel, var type: Int = RouteStateConstants.POINT_TYPE_DEFAULT): Parcelable