package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
@Parcelize
data class AddressModel(val value: String,
                        val unrestrictedValue: String,
                        val lat: Double?,
                        val lng: Double?): Parcelable