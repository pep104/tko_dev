package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-02-03
 * Project: tko-android
 */
@Parcelize
data class ContainerModel(
    val id: Int?,
    val type: String,
    val loading: ContainerLoading,
    val volume: Double?,
) : Parcelable