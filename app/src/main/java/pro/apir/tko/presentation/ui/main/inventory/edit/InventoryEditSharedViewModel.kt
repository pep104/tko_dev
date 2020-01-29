package pro.apir.tko.presentation.ui.main.inventory.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.presentation.platform.livedata.LiveEvent

/**
 * Created by Антон Сарматин
 * Date: 23.01.2020
 * Project: tko-android
 */
class InventoryEditSharedViewModel : ViewModel() {

    private val _containerArea = LiveEvent<ContainerAreaShortModel>()
    val containerArea: LiveData<ContainerAreaShortModel>
        get() = _containerArea

    fun setContainer(containerAreaShortModel: ContainerAreaShortModel) {
        _containerArea.postValue(containerAreaShortModel)
    }

}