package pro.apir.tko.presentation.ui.main.inventory.edit

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.presentation.entities.PhotoWrapper
import pro.apir.tko.presentation.extension.notifyObserver
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.platform.BaseViewModelExperimental
import pro.apir.tko.presentation.platform.livedata.LiveEvent
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
//TODO REFACTOR FORM PROCESSING
class InventoryEditViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle, private val inventoryInteractor: InventoryInteractor) : BaseViewModel(handle) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryEditViewModel>

    private val _isNewMode = handle.getLiveData<Boolean>("mode", true)
    val isNewMode: LiveData<Boolean>
        get() = _isNewMode

    private val _isSaved = LiveEvent<Boolean>()
    val isSaved: LiveData<Boolean>
        get() = _isSaved

    private val _containerArea = handle.getLiveData<ContainerAreaShortModel>("containerArea", ContainerAreaShortModel())
    val containerArea: LiveData<ContainerAreaShortModel>
        get() = _containerArea


    //TODO FIELD VALUES HERE from handle (not LiveData)
    //TODO FIELD STATE LIVEDATA

    private val _images = handle.getLiveData<MutableList<PhotoWrapper>>("images", mutableListOf())
    val images: LiveData<MutableList<PhotoWrapper>>
        get() = _images


    fun setEditData(data: ContainerAreaShortModel?) {
        if (data != null) {
            _isNewMode.value = false
            _containerArea.value = data
            if (data.photos != null)
                _images.value = data.photos?.map { PhotoWrapper(it) }?.toMutableList()
        }
    }

    fun addNewPhotos(list: List<File>) {
        _images.value?.addAll(list.map { PhotoWrapper(it) })
        _images.notifyObserver()
    }


    fun deletePhoto(photoWrapper: PhotoWrapper) {
        _images.value?.let {
            it.remove(photoWrapper)
            _images.notifyObserver()
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
            loading(true)
            delay(500)
            _containerArea.value?.let {
                val oldPhotos = _images.value?.filter { it.uploaded != null }?.map { it.uploaded!! }
                val newPhotos = _images.value?.filter { it.new != null }?.map { it.new!! }
                inventoryInteractor.updateContainer(it, oldPhotos, newPhotos).fold(::handleFailure) {
                    _isSaved.postValue(true)
                }
            }
        }
    }

}