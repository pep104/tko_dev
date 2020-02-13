package pro.apir.tko.presentation.ui.main.list.inventory

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.data.framework.manager.location.LocationManager
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.presentation.ui.main.list.BaseListViewModel

class InventoryListViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                         private val inventoryInteractor: InventoryInteractor,
                                                         private val locationManager: LocationManager) : BaseListViewModel(handle, inventoryInteractor, locationManager) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryListViewModel>


}
