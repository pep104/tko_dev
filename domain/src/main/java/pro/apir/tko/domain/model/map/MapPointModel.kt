package pro.apir.tko.domain.model.map

import kotlinx.android.parcel.Parcelize
import pro.apir.tko.domain.model.CoordinatesModel

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */
@Parcelize
data class MapPointModel(
    val coordinates: CoordinatesModel,
    val url: String
)
