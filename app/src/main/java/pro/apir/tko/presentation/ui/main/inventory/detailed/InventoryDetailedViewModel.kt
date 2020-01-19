package pro.apir.tko.presentation.ui.main.inventory.detailed

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-19
 * Project: tko-android
 */
class InventoryDetailedViewModel @AssistedInject constructor(@Assisted handle: SavedStateHandle) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryDetailedViewModel>


}