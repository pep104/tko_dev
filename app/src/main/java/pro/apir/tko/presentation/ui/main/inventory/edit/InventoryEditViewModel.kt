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
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.domain.model.ContainerAreaParametersModel
import pro.apir.tko.domain.model.ImageModel
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class InventoryEditViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle, private val inventoryInteractor: InventoryInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryEditViewModel>

    private val _isNewMode = handle.getLiveData<Boolean>("mode", true)
    val isNewMode: LiveData<Boolean>
        get() = _isNewMode

    private val _containerArea = handle.getLiveData<ContainerAreaDetailedModel>("containerArea")
    val containerArea: LiveData<ContainerAreaDetailedModel>
        get() = _containerArea

    private val _images = handle.getLiveData<List<ImageModel>>("images")
    val images: LiveData<List<ImageModel>>
        get() = _images

    fun setEditData(data: ContainerAreaDetailedModel) {
        _isNewMode.value = false
        _containerArea.value = data
        getAllImages(data.parameters)
    }

    //TODO FIELDS SAVE STATE

    //TODO PHOTOS
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

    //???

    fun deletePhoto(image: Int){
        //TODO REMOVE FROM container

    }


    fun testSave(reg: String, adress: String){
        viewModelScope.launch(Dispatchers.IO) { inventoryInteractor.updateContainer(_containerArea.value!!) }
    }

}