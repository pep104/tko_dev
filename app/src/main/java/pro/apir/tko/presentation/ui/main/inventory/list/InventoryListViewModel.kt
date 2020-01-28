package pro.apir.tko.presentation.ui.main.inventory.list

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.platform.BaseViewModel

class InventoryListViewModel @AssistedInject constructor(@Assisted handle: SavedStateHandle, private val inventoryInteractor: InventoryInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryListViewModel>

    private var fetchJob: Job? = null

    private val _containers = handle.getLiveData<List<ContainerAreaListModel>>("containers")
    val containers: LiveData<List<ContainerAreaListModel>>
        get() = _containers


    private var _lastPosition = handle.get<IGeoPoint>("bbox")
    val lastPosition: IGeoPoint?
        get() = _lastPosition

    private var _zoomLevel = handle.get<Double>("zoomLevel")
    val zoomLevel: Double?
        get() = _zoomLevel

    private val testLngMin = 49.00
    private val testLatMin = 55.72
    private val testLngMax = 49.27
    private val testLatMax = 55.86

    fun testGet() {
//        viewModelScope.launch {
//            if (_containers.value.isNullOrEmpty()) {
////                inventoryInteractor.getContainerAreas(1, 500, "Казань").fold(::handleFailure) {
////                    _containers.postValue(it)
////                }
//
//                inventoryInteractor.getContainerAreasByBoundingBox(testLngMin, testLatMin, testLngMax, testLatMax, 1, 500).fold(::handleFailure) {
//                    _containers.postValue(it)
//                }
//
//            }
//        }
    }


    fun fetch(lngMin: Double, latMin: Double, lngMax: Double, latMax: Double) {
        //TODO COUNT DELTA?
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            inventoryInteractor.getContainerAreasByBoundingBox(lngMin, latMin, lngMax, latMax, 1, 500).fold(::handleFailure) {
                _containers.postValue(it)
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
        //todo combine with existing?


    }


}
