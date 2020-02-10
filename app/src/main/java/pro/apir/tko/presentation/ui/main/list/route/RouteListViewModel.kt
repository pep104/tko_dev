package pro.apir.tko.presentation.ui.main.list.route

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.route.RouteInteractor
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.extension.notifyObserver
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

    private var page = handle.get<Int>("page")
        set(value) {
            field = value
            handle.set("page", value)
        }

    val pageSize = 20

    val isPageLoading: Boolean
        get() {
            return pagingJob?.isActive == true
        }

    private var _isLastPage = false
    val isLastPage: Boolean
        get() = _isLastPage


    private val _routes = handle.getLiveData<MutableList<RouteModel>>("routes", mutableListOf())
    val routes: LiveData<List<RouteModel>>
        get() = Transformations.map(_routes) { it.toList() }

    private val _choosenRoute = handle.getLiveData<RouteModel?>("choosenRoute", null)
    val choosenRoute: LiveData<RouteModel?>
        get() = _choosenRoute

    init {
        loading(true)
        loadMore()
    }

    //???
    fun fetchMore() {
        loadMore()
    }

    fun setChosenRoute(id: Int?){
        Log.e("choose","id: $id")
        _routes.value?.let { list ->
            val item = list.find { model -> model.id == id }
            _choosenRoute.postValue(item)
        }
    }

    private fun loadMore() {
        pagingJob = viewModelScope.launch {
            routeInteractor.getRoutesList(page ?: 1, pageSize).fold(::handleFailure) {
                loading(false)
                if (it.isNotEmpty()) {
                    _routes.value?.addAll(it.toMutableList())
                    _routes.notifyObserver()
                    page = page?.plus(1)
                    if (it.size > pageSize) {
                        _isLastPage = true
                    }
                }

            }
        }

    }

}