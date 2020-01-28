package pro.apir.tko.presentation.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import pro.apir.tko.domain.model.ImageModel
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */
//TODO file or path?
//TODO AS EITHER?
@Parcelize
data class PhotoWrapper(val uploaded: ImageModel?, val new: File?): Parcelable {

    constructor(uploaded: ImageModel) : this(uploaded, null)

    constructor(new: File) : this(null, new)

}