package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-02-13
 * Project: tko-android
 */
@Parcelize
data class LocationModel(val lat: Double, val lon: Double): Parcelable