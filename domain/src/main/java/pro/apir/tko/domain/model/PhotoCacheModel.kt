package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-03-29
 * Project: tko-android
 */
@Parcelize
data class PhotoCacheModel(val id: Long, val pointId: Long, val sessionId: Long, val path: String): Parcelable