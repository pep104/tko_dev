package pro.apir.tko.presentation.ui.main.inventory.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.platform.BaseViewModel

class InventoryListViewModel @AssistedInject constructor(@Assisted handle: SavedStateHandle, private val inventoryInteractor: InventoryInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryListViewModel>

    private var fetchJob: Job? = null
    private var searchJob: Job? = null

    private val _containersRaw = handle.get<List<ContainerAreaListModel>>("containerRaw")

    private val _containers = handle.getLiveData<List<ContainerAreaListModel>>("containers")
    val containers: LiveData<List<ContainerAreaListModel>>
        get() = _containers

    private var _lastPosition = handle.get<IGeoPoint>("bbox")
    val lastPosition: IGeoPoint?
        get() = _lastPosition

    private var _zoomLevel = handle.get<Double>("zoomLevel")
    val zoomLevel: Double?
        get() = _zoomLevel

    private val _searchMode = handle.getLiveData<Boolean>("searchMode", false)
    val searchMode: LiveData<Boolean>
        get() = _searchMode

    private var _searchQuery = handle.get<String>("searchQuery")
    val searchQuery: String?
        get() = _searchQuery

    private val testLngMin = 49.00
    private val testLatMin = 55.72
    private val testLngMax = 49.27
    private val testLatMax = 55.86


    fun fetch(lngMin: Double, latMin: Double, lngMax: Double, latMax: Double) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            inventoryInteractor.getContainerAreasByBoundingBox(lngMin, latMin, lngMax, latMax, 1, 500).fold(::handleFailure) {
                combineAreas(it)
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

    fun query(query: String){
        val current = _searchMode.value
        if(current!= null && current){
            searchJob?.cancel()
            searchJob = viewModelScope.launch(Dispatchers.IO) {
                delay(200)
                //TODO
            }
        }
    }

    fun setZoomLevel(zoomLevel: Double) {
        _zoomLevel = zoomLevel
    }

    fun setLocation(geoPoint: IGeoPoint?) {
        _lastPosition = geoPoint
    }

    private fun combineAreas(newList: List<ContainerAreaListModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = mutableListOf<ContainerAreaListModel>()
            containers.value?.let { existing.addAll(it) }
            val combined = existing.union(newList)
            _containers.postValue(combined.toList())
        }
    }


}
