package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
sealed class PhotoModel {
    @Parcelize
    data class LocalFile(val path: String): PhotoModel(), Parcelable

    @Parcelize
    data class RemoteFile(val url: String): PhotoModel(), Parcelable
}