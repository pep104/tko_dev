package pro.apir.tko.presentation.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
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
        var volume: Double?
) : Parcelable {

    //Maps from domain model
    constructor(model: ContainerModel) : this(model.id, false, model.type, model.volume)

    //Create new container
    constructor(type: String, volume: Double?) : this(Random.nextInt(1000, 9999), true, type, volume)

    //Maps to domain model
    fun toModel(): ContainerModel {
        return if (isNew) {
            ContainerModel(null, type, volume)
        } else {
            ContainerModel(id, type, volume)
        }
    }

}