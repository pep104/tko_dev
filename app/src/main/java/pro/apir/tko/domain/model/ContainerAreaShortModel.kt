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
        val coordinates: CoordinatesModel?,
        val location: String?,
        val registryNumber: String?,
        val photos: List<ImageModel>?,
        val hasCover: Boolean?,
        val infoPlate: Boolean?,
        val access: String?,
        val fence: String?,
        val coverage: String?,
        val kgo: String?
) : Parcelable {

    //todo edit constructor

}