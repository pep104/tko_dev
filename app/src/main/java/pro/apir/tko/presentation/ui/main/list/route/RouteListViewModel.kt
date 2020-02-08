package pro.apir.tko.presentation.ui.main.list.route

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.route.RouteInteractor
import pro.apir.tko.presentation.ui.main.list.BaseListViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-08
 * Project: tko-android
 */
class RouteListViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                    private val inventoryInteractor: InventoryInteractor,
                                                    private val routeInteractor: RouteInteractor) : BaseListViewModel(handle, inventoryInteractor) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<RouteListViewModel>


}