package pro.apir.tko.presentation.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import pro.apir.tko.domain.model.ContainerAreaStopModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */

const val ROUTE_TYPE_DEFAULT = 0
const val ROUTE_TYPE_PENDING = 1
const val ROUTE_TYPE_COMPLETED = 2

@Parcelize
data class RouteStop(val stop: ContainerAreaStopModel, var type: Int = ROUTE_TYPE_DEFAULT): Parcelable