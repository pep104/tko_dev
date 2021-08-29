package pro.apir.tko.presentation.entities

import androidx.annotation.Keep
import androidx.annotation.StringRes
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerLoading

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */
@Keep
enum class ContainerLoadingUi(@StringRes val stringId: Int) : Selector {
    SIDE(R.string.container_loading_side),
    REAR(R.string.container_loading_rear),
    FRONT(R.string.container_loading_front),
    PORTAL(R.string.container_loading_portal),
    CRANE(R.string.container_loading_crane),
    MULTILIFT(R.string.container_loading_multilift);

    override fun stringId(): Int {
        return this.stringId
    }

    override fun key(): String {
        return this.name
    }


    fun toModel() = when (this) {
        SIDE -> ContainerLoading.SIDE
        REAR -> ContainerLoading.REAR
        FRONT -> ContainerLoading.FRONT
        PORTAL -> ContainerLoading.PORTAL
        CRANE -> ContainerLoading.CRANE
        MULTILIFT -> ContainerLoading.MULTILIFT
    }

    companion object {

        fun fromModel(model: ContainerLoading) = when (model) {
            ContainerLoading.SIDE -> SIDE
            ContainerLoading.REAR -> REAR
            ContainerLoading.FRONT -> FRONT
            ContainerLoading.PORTAL -> PORTAL
            ContainerLoading.CRANE -> CRANE
            ContainerLoading.MULTILIFT -> MULTILIFT
        }

    }

}