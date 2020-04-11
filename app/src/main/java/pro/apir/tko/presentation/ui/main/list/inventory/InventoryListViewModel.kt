package pro.apir.tko.presentation.ui.main.list.inventory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.apir.tko.data.framework.manager.location.LocationManager
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.ui.main.inventory.edit.EditResultEvent
import pro.apir.tko.presentation.ui.main.list.BaseListViewModel

class InventoryListViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                         private val inventoryInteractor: InventoryInteractor,
                                                         private val locationManager: LocationManager) : BaseListViewModel(handle, inventoryInteractor, locationManager) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryListViewModel>




    fun handleEditResult(result: EditResultEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (result) {
                is EditResultEvent.Edited -> {
                    //FIND SAME CONTAINER MODEL
                    //UPD
                    val existing = containers.value?.toMutableList()
                    existing?.let { list ->
                        val index = list.indexOfFirst { it.id == result.model.id?.toLong() }
                        if (index != -1) {
                            val old = list[index]
                            list.removeAt(index)
                            list.add(index, ContainerAreaListModel(old, result.model))
                            _containers.postValue(list)
                        }
                    }
                }
                is EditResultEvent.Created -> {
                    //INSERT TO START
                    val containersList = mutableListOf<ContainerAreaListModel>()
                    containersList.add(ContainerAreaListModel(result.model))
                    containers.value?.let { containersList.addAll(it) }
                    _containers.postValue(containersList)
                }
            }
        }
    }

}
