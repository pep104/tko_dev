package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
@Parcelize
data class ContainerAreaListModel(
        val id: Long,
        var identifier: String,
        var registryNumber: String,
        var location: String,
        var status: String,
        var coordinates: CoordinatesModel?,
        var containersCount: Int,
        var area: Double = 0.0,
        var resourceType: String
): Parcelable