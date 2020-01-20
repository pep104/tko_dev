package pro.apir.tko.presentation.ui.main.inventory.detailed

import android.media.Image
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.domain.model.ContainerAreaParametersModel
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

    private val _data = handle.getLiveData<ContainerAreaDetailedModel>("data")
    val data: LiveData<ContainerAreaDetailedModel>
        get() = _data

    private val _images = handle.getLiveData<List<ImageModel>>("images")
    val images: LiveData<List<ImageModel>>
        get() = _images

    private val _header = handle.getLiveData<String>("header")
    val header: LiveData<String>
        get() = _header

    fun fetchInfo(id: Long, header: String) {
        setHeader(header)
        if (_data.value == null) {
            loading(true)
            viewModelScope.launch(Dispatchers.IO) {
                val result = inventoryInteractor.getContainerDetailed(id).fold(::handleFailure) {
                    _data.postValue(it)
                    getAllImages(it.parameters)
                    loading(false)
                }
            }
        }
    }

    //TODO Extract?
    private fun getAllImages(params: List<ContainerAreaParametersModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = arrayListOf<ImageModel>()
            params.forEach {
                result.addAll(it.photos)
            }
            _images.postValue(result)
        }
    }

    private fun setHeader(header: String) {
        if (_header.value == null) {
            _header.postValue(header)
        }
    }

}