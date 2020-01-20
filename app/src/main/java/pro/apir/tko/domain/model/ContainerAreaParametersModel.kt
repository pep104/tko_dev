package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
@Parcelize
data class ContainerAreaParametersModel(
        val id: Long,
        val photos: List<ImageModel>
): Parcelable