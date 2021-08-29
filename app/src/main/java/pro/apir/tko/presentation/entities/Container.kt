package pro.apir.tko.presentation.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import pro.apir.tko.domain.model.ContainerLoading
import pro.apir.tko.domain.model.ContainerModel
import kotlin.random.Random

/**
 * Created by Антон Сарматин
 * Date: 05.02.2020
 * Project: tko-android
 */
@Parcelize
data class Container(
    val id: Int?,
    val isNew: Boolean = false,
    var type: String,
    var loading: ContainerLoading,
    var volume: Double?,
) : Parcelable {

    //Maps from domain model
    constructor(model: ContainerModel) : this(
        model.id,
        false,
        model.type,
        model.loading,
        model.volume
    )

    //Create new container
    constructor(
        type: String,
        loading: ContainerLoading,
        volume: Double?,
    ) : this(
        Random.nextInt(1000, 9999),
        true,
        type,
        loading,
        volume)

    //Maps to domain model
    fun toModel(): ContainerModel {
        return if (isNew) {
            ContainerModel(null, type, loading, volume)
        } else {
            ContainerModel(id, type, loading, volume)
        }
    }

}