package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
@Parcelize
data class PhotoModel(val id: Long?, val type: Type, val path: String) : Parcelable {

    enum class Type {
        LOCAL, REMOTE
    }


    constructor(model: PhotoCacheModel) : this(model.id, Type.LOCAL, model.path)
}