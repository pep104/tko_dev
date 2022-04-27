package pro.apir.tko.presentation.ui.main.inventory.detailed

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.presentation.platform.BaseViewModel
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 2020-01-19
 * Project: tko-android
 */
//TODO EXTRACT CONTROLS etc TO BASE DETAILED VM
@HiltViewModel
class InventoryDetailedViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val inventoryInteractor: InventoryInteractor,
) : BaseViewModel() {


    private val _data = handle.getLiveData<ContainerAreaShortModel>("data")
    val data: LiveData<ContainerAreaShortModel>
        get() = _data

    private val _header = handle.getLiveData<String>("header")
    val header: LiveData<String>
        get() = _header

    private val _coordingates = handle.getLiveData<CoordinatesModel>("coordinates")
    val coordinates: LiveData<CoordinatesModel>
        get() = _coordingates

    private val _isFollowEnabled = handle.getLiveData<Boolean>("isFollowEnabled", false)
    val isFollowEnabled: LiveData<Boolean>
        get() = _isFollowEnabled

    fun fetchInfo(id: Long, header: String, coordinates: CoordinatesModel) {
        setHeader(header)
        setCoordinates(coordinates)
        if (_data.value == null) {
            loading(true)
            viewModelScope.launch(Dispatchers.IO) {
                inventoryInteractor.getContainerArea(id).fold(::handleFailure) {
                    _data.postValue(it)
                    loading(false)
                }
            }
        }
    }

    fun setData(containerAreaShortModel: ContainerAreaShortModel) {
        _data.postValue(containerAreaShortModel)
    }

    fun setEditedData(containerAreaShortModel: ContainerAreaShortModel) {
        setData(containerAreaShortModel)
        val header = containerAreaShortModel.location ?: ""
        setHeader(header, true)
        containerAreaShortModel.coordinates?.let {
            setCoordinates(it, true)
        }
    }

    //On some situations MapView disables follow, so we need to disable it in VM
    fun disableFollow() {
        if (_isFollowEnabled.value == true) {
            _isFollowEnabled.value = false
        }
    }

    fun switchFollow() {
        _isFollowEnabled.value?.let {
            _isFollowEnabled.value = !it
        }
    }

    private fun setHeader(header: String, force: Boolean = false) {
        if (_header.value == null || force) {
            _header.postValue(header)
        }
    }

    private fun setCoordinates(coordinates: CoordinatesModel, force: Boolean = false) {
        if ((_data.value == null && _coordingates.value == null) || force) {
            _coordingates.postValue(coordinates)
        }
    }

}