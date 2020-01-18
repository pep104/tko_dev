package pro.apir.tko.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
@Parcelize
data class ContainerModel(
        val id: Int,
        val access: String?,
        val area: Double?,
        val closeAt: String?,
        val containersCount: Int?,
        val coordinates: CoordinatesModel?,
        val coverageType: String?,
        val eventsCount: Int?,
        val fence: String?,
        val fullnessPercent: String?,
        val has_cover: Boolean?,
        val identifier: String?,
        val informationPlate: Boolean?,
        val lastUpdateAt: String?,
        val lastUpdatePerson: String?,
        val length: Double?,
        val location: String?,
        val otherBasementDescription: String?,
        val ownersNames: String?,
        val ownersWasteSourcesNames: String?,
        val platform_basement: String?,
        val registry_number: String?,
        val remoteness: String?,
//        val responsiblePerson: Any,
        val sourcesCount: Int?,
        val status: String? = "this was null",
        val totalNormative: Double?,
        val width: Double?
): Parcelable