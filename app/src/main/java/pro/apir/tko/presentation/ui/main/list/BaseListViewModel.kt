package pro.apir.tko.presentation.ui.main.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.map.MapPointInteractor
import pro.apir.tko.domain.manager.LocationManager
import pro.apir.tko.domain.model.BBoxModel
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.domain.model.LocationModel
import pro.apir.tko.domain.model.map.MapPointModel
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by Антон Сарматин
 * Date: 08.02.2020
 * Project: tko-android
 */
abstract class BaseListViewModel(
    private val handle: SavedStateHandle,
    private val mapInteractor: MapPointInteractor,
    private val inventoryInteractor: InventoryInteractor,
    private val locationManager: LocationManager,
) : BaseViewModel() {

    protected var mapJob: Job? = null
    protected var fetchJob: Job? = null
    protected var searchJob: Job? = null


    private val _allMapPoints = handle.getLiveData<List<MapPointModel>>("allMapPicture")
    val allMapPoints: LiveData<List<MapPointModel>>
        get() = _allMapPoints

    private val _newMapPoints = MutableLiveData<List<MapPointModel>>()
    val newMapPoints: LiveData<List<MapPointModel>>
        get() = _newMapPoints

    //hotfix handle.getLiveData<List<ContainerAreaListModel>>("containers") т.к. может вызвать ошибку по памяти в парселе
    protected val _containers = MutableLiveData<List<ContainerAreaListModel>>()
    val containers: LiveData<List<ContainerAreaListModel>>
        get() = _containers

    protected val _searchContainersResults =
        handle.getLiveData<List<ContainerAreaListModel>>("searchResults")
    val searchContainersResults: LiveData<List<ContainerAreaListModel>>
        get() = _searchContainersResults

    protected val _searchLoading = handle.getLiveData<Boolean>("searchLoading")
    val searchLoading: LiveData<Boolean>
        get() = _searchLoading

    protected var _lastPosition = handle.get<IGeoPoint>("bbox")
        set(value) {
            handle.set("bbox", value)
            if (value != null) {
                locationManager.saveLocalLocation(LocationModel(value.latitude, value.longitude))
            }
            field = value
        }

    val lastPosition: IGeoPoint?
        get() = _lastPosition

    protected var _zoomLevel = handle.get<Double>("zoomLevel")
        set(value) {
            handle.set("zoomLevel", value)
            field = value
        }

    val zoomLevel: Double?
        get() = _zoomLevel

    protected val _searchMode = handle.getLiveData<Boolean>("searchMode", false)
    val searchMode: LiveData<Boolean>
        get() = _searchMode

    private var _searchQuery = handle.get<String>("searchQuery")
    val searchQuery: String?
        get() = _searchQuery

    init {
        if (lastPosition == null) {
            locationManager.geLocalLocation()?.let {
                _lastPosition = GeoPoint(it.lat, it.lon)
            }
        }
        collectMapPoints()
    }

    fun fetchMapPoints(bbox: BBoxModel) {
        mapJob?.cancel()
        mapJob = viewModelScope.launch(Dispatchers.IO) {
            mapInteractor.fetch(bbox)
        }
    }

    private fun collectMapPoints() {
        viewModelScope.launch(Dispatchers.IO) {
            mapInteractor.getMapPoints()
                .collectLatest { points ->
                    launch {
                        val current = _allMapPoints.value ?: emptyList()
                        val new = points.filter { !current.contains(it) }
                        _newMapPoints.postValue(new)
                        _allMapPoints.postValue(current + new)
                    }
                }
        }
    }


    fun fetchContainerAreas(bbox: BBoxModel) {
        fetchJob?.cancel()
        if (_searchMode.value != true) {
            fetchJob = viewModelScope.launch(Dispatchers.IO) {
                inventoryInteractor.getContainerAreasByBoundingBox(bbox)
                    .collectLatest {
                        it.fold(::handleFailure) {
                            combineAreas(it)
                        }
                    }
            }
        }
    }

    fun switchSearchMode() {
        searchJob?.cancel()
        val current = _searchMode.value
        if (current != null) {
            _searchMode.value = !current
        } else {
            _searchMode.value = false
        }
    }

    open fun searchQuery(string: String) {
        if (string.isNullOrBlank()) {
            _searchContainersResults.postValue(emptyList())
        } else {
            query {
                _searchLoading.postValue(true)
                inventoryInteractor.searchContainerArea(string).fold(::handleFailure) {
                    _searchContainersResults.postValue(it)
                    _searchLoading.postValue(false)
                }
            }
        }
    }

    protected fun query(function: suspend (scope: CoroutineScope) -> Unit) {
        if (_searchMode.value == true) {
            fetchJob?.cancel()
            searchJob?.cancel()
            searchJob = viewModelScope.launch(Dispatchers.IO) {
                delay(200)
                function.invoke(this)
            }
        }
    }

    fun setZoomLevel(zoomLevel: Double) {
        _zoomLevel = zoomLevel

    }

    fun setLocation(geoPoint: IGeoPoint?) {
        _lastPosition = geoPoint
    }

    protected fun combineAreas(newList: List<ContainerAreaListModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = mutableListOf<ContainerAreaListModel>()
            containers.value?.let { existing.addAll(it) }
            val combined = existing.union(newList)
            _containers.postValue(combined.toList())
        }
    }

}