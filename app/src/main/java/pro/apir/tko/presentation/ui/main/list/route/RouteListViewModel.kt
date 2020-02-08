package pro.apir.tko.presentation.ui.main.list.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.route.RouteInteractor
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.ui.main.list.BaseListViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-08
 * Project: tko-android
 */
class RouteListViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                     inventoryInteractor: InventoryInteractor,
                                                     private val routeInteractor: RouteInteractor) : BaseListViewModel(handle, inventoryInteractor) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<RouteListViewModel>

    private var pagingJob: Job? = null

    //TODO PAGING
    private var page = handle.get<Int>("page")
        set(value) {
            handle.set("page", value)
            field = value
        }

    private val pageSize = 100

    private val _routes = handle.getLiveData<List<RouteModel>>("routes")
    val routes: LiveData<List<RouteModel>>
        get() = _routes


    init {
        pagingJob = viewModelScope.launch(Dispatchers.IO) {
            loadMore()
        }
    }

    fun fetchMore() {
        //TODO PAGING

    }

    private suspend fun loadMore() {
        //TODO PAGING
        routeInteractor.getRoutesList(page ?: 1, pageSize).fold(::handleFailure) {
            _routes.postValue(it)
        }

    }

}