package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
@Parcelize
data class ImageModel(val side: String,
                      val image: Int,
                      val url: String): Parcelable