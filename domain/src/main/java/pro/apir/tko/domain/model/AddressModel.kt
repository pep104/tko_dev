package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
@Parcelize
data class AddressModel(
        val value: String,
        val unrestrictedValue: String,

        val lat: Double?,
        val lng: Double?,

        val cityType: String?,
        val cityTypeFull: String?,
        val city: String?,
        val cityFull: String?,

        val streetType: String?,
        val streetTypeFull: String?,
        val street: String?,
        val streetFull: String?,

        val houseType: String?,
        val houseTypeFull: String?,
        val house: String?,
        val houseFull: String?,
        val fiasLevel: Int,

        val isUserLocation: Boolean = false

) : Parcelable {

    val isContainsCity: Boolean
        get() = city != null && fiasLevel >= 4

    val isContainsStreet: Boolean
        get() = street != null && fiasLevel >= 7

    // OR - для некоторых адресов level == 7, но дом есть.
    val isContainsHouse: Boolean
        get() = house != null || fiasLevel >= 8
}