package pro.apir.tko.presentation.ui.main.inventory.detailed

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.domain.model.ImageModel
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-19
 * Project: tko-android
 */
class InventoryDetailedViewModel @AssistedInject constructor(@Assisted handle: SavedStateHandle, private val inventoryInteractor: InventoryInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryDetailedViewModel>

    private val _data = handle.getLiveData<ContainerAreaShortModel>("data")
    val data: LiveData<ContainerAreaShortModel>
        get() = _data

    private val _header = handle.getLiveData<String>("header")
    val header: LiveData<String>
        get() = _header

    private val _coordingates = handle.getLiveData<CoordinatesModel>("coordinates")
    val coordinates: LiveData<CoordinatesModel>
        get() = _coordingates

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

    fun setData(containerAreaShortModel: ContainerAreaShortModel){
        _data.postValue(containerAreaShortModel)
    }



    private fun setHeader(header: String) {
        if (_header.value == null) {
            _header.postValue(header)
        }
    }

    private fun setCoordinates(coordinates: CoordinatesModel) {
        if (_data.value == null && _coordingates.value == null) {
            _coordingates.postValue(coordinates)
        }
    }

}