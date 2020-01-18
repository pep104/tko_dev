package pro.apir.tko.presentation.ui.main.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.ContainerModel
import pro.apir.tko.presentation.platform.BaseViewModel

class InventoryListViewModel @AssistedInject constructor(@Assisted handle: SavedStateHandle, private val inventoryInteractor: InventoryInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryListViewModel>

    private val _containers = handle.getLiveData<List<ContainerModel>>("containers")
    val containers: LiveData<List<ContainerModel>>
        get() = _containers

    fun testGet() {
        viewModelScope.launch {
            if(_containers.value.isNullOrEmpty()){
                inventoryInteractor.getContainers(1, 500, "Казань").fold(::handleFailure) {
                    _containers.postValue(it)
                }
            }
        }
    }

}
