package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
@Parcelize
data class CoordinatesModel(val lng: Double, val lat: Double): Parcelable