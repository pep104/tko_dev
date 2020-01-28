package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
@Parcelize
data class ContainerAreaShortModel(
        val id: Int?,
        val area: Double?,
        val containersCount: Int?,
        var coordinates: CoordinatesModel?,
        var location: String?,
        var registryNumber: String?,
        var photos: List<ImageModel>?
) : Parcelable {

    constructor() : this(null, null, null, null,null, null, null)

}