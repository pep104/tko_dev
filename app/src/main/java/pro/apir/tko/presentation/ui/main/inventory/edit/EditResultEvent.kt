package pro.apir.tko.presentation.ui.main.inventory.edit

import pro.apir.tko.domain.model.ContainerAreaShortModel

/**
 * Created by antonsarmatin
 * Date: 2020-04-08
 * Project: tko-android
 */
sealed class EditResultEvent(val model: ContainerAreaShortModel) {

    class Edited(model: ContainerAreaShortModel) : EditResultEvent(model)

    class Created(model: ContainerAreaShortModel) : EditResultEvent(model)

}