package pro.apir.tko.presentation.ui.main.list.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pro.apir.tko.data.framework.network.api.RouteTrackApi
import pro.apir.tko.data.framework.room.dao.RouteSessionDao
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.route.RouteInteractor
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractor
import pro.apir.tko.domain.manager.LocationManager
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.extension.notifyObserver
import pro.apir.tko.presentation.ui.main.list.BaseListViewModel
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 2020-02-08
 * Project: tko-android
 */
@HiltViewModel
class RouteListViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val inventoryInteractor: InventoryInteractor,
    private val routeInteractor: RouteInteractor,
    private val routeSessionInteractor: RouteSessionInteractor,
    private val locationManager: LocationManager,
    private val routeTrackApi: RouteTrackApi,
    private val routeSessionDao: RouteSessionDao,
) : BaseListViewModel(handle, inventoryInteractor, locationManager) {

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

    private val _searchRoutes = handle.getLiveData<MutableList<RouteModel>>("searchRoutes")
    val searchRoutes: LiveData<List<RouteModel>>
        get() = Transformations.map(_searchRoutes) { it.toList() }

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

    fun setChosenRoute(id: Int?) {
        _routes.value?.let { list ->
            val item = list.find { model -> model.id == id }
            _choosenRoute.postValue(item)
        }
    }

    fun refreshChosen() {
        viewModelScope.launch {
            val routes = _routes.value
            routes?.let { list ->
                routeSessionInteractor.mapRouteListWithExisting(list).fold(::handleFailure) {
                    _routes.postValue(it.toMutableList())
                }
            }
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

    override fun searchQuery(string: String) {
        if (string.isBlank()) {
            _searchContainersResults.postValue(emptyList())
        } else {
            query {
                _searchLoading.postValue(true)
                val inventory = it.async {
                    inventoryInteractor.searchContainerArea(string).fold(::handleFailure) {
                        _searchContainersResults.postValue(it)
                    }
                }
                val route = it.async {
                    routeInteractor.searchRoutes(string).fold(::handleFailure) {
                        _searchRoutes.postValue(it.toMutableList())
                    }
                }
                inventory.await()
                route.await()
                _searchLoading.postValue(false)
            }
        }
    }


}