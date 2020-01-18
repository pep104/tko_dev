package pro.apir.tko.presentation.ui.main.inventory

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.presentation.platform.BaseViewModel

class InventoryListViewModel @AssistedInject constructor(@Assisted handle: SavedStateHandle) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory: ViewModelAssistedFactory<InventoryListViewModel>

}
