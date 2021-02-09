package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 08.02.2020
 * Project: tko-android
 */
@Parcelize
data class ContainerAreaStopModel(
        val id: Int,
        val entityId: Int,
        val resourceType: String,
        val location: String?,
        val coordinates: CoordinatesModel?,
        val registryNumber: String?,
        val containersCount: Int?,
        val containersVolume: Double?
): Parcelable