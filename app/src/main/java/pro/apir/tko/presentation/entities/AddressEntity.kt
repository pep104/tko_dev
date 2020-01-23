package pro.apir.tko.presentation.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 23.01.2020
 * Project: tko-android
 */
@Parcelize
data class AddressEntity(val name: String, val lat: Double, val lon: Double): Parcelable