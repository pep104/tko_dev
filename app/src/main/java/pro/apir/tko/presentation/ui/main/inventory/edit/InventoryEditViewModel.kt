package pro.apir.tko.presentation.ui.main.inventory.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.*
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
//TODO REFACTOR FORM PROCESSING
class InventoryEditViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle, private val inventoryInteractor: InventoryInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryEditViewModel>

    private val _isNewMode = handle.getLiveData<Boolean>("mode", true)
    val isNewMode: LiveData<Boolean>
        get() = _isNewMode

    private val _containerArea = handle.getLiveData<ContainerAreaShortModel>("containerArea", ContainerAreaShortModel())
    val containerArea: LiveData<ContainerAreaShortModel>
        get() = _containerArea

    private val _images = handle.getLiveData<List<ImageModel>>("images")
    val images: LiveData<List<ImageModel>>
        get() = _images

    fun setEditData(data: ContainerAreaShortModel?) {
        if(data != null){
            _isNewMode.value = false
            _containerArea.value = data
            getAllImages(data.parameters)
        }
    }


    //???
    fun deletePhoto(image: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _containerArea.value?.let {
                it.parameters.forEach {
                    it.photos.removeAll { it.image == image }
                }
                getAllImages(it.parameters)
            }

        }
    }

    fun updateRegNum(text: String) {
        _containerArea.value?.let {
            it.registryNumber = text
        }
    }

    fun updateAddress(addressModel: AddressModel) {
        _containerArea.value?.let {
            it.location = addressModel.value
            if (addressModel.lng != null && addressModel.lat != null)
                it.coordinates = CoordinatesModel(addressModel.lng, addressModel.lat)
            else
                it.coordinates = null
        }
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            _containerArea.value?.let {
                inventoryInteractor.updateContainer(it).fold(::handleFailure) {
                    //TODO RESULT
                }
            }
        }
    }

    //
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

}