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
import pro.apir.tko.core.types.Dictionary
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.presentation.dict.OptionsDictionariesManager
import pro.apir.tko.presentation.entities.PhotoWrapper
import pro.apir.tko.presentation.extension.notifyObserver
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.platform.livedata.LiveEvent
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class InventoryEditViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                         private val inventoryInteractor: InventoryInteractor,
                                                         private val dictionariesManager: OptionsDictionariesManager) : BaseViewModel(handle) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryEditViewModel>

    private val _isNewMode = handle.getLiveData<Boolean>("mode", true)
    val isNewMode: LiveData<Boolean>
        get() = _isNewMode

    private val _isSaved = LiveEvent<ContainerAreaShortModel>()
    val isSaved: LiveData<ContainerAreaShortModel>
        get() = _isSaved


    //TODO FIELD STATE LIVEDATA

    //Editable data

    private var _id = handle.get<Int>("id")

    var area = handle.get<Double>("cArea")
        set(value) {
            field = value
            handle.set("cArea", value)
        }

    private val _address = handle.getLiveData<AddressModel>("cAddress")
    val address: LiveData<AddressModel>
        get() = _address

    var registryNumber = handle.get<String>("cRegistryNumber")
        set(value) {
            field = value
            handle.set("cRegistryNumber", value)
        }

    //TODO T/F HAS COVER
    //TODO T/F INFO PLATE
//    val basementOptions: LiveData<Dictionary> = MutableLiveData(dictionariesManager.getBasementOptionsDictionary())
//    var basement = handle.get<Int>("basement")
//        set(value) {
//            field = value
//            handle.set("basement", value)
//        }

    //    val remotenessOptions: LiveData<Dictionary> = MutableLiveData(dictionariesManager.getRemotenessOptionsDictionary())
//    var remoteness = handle.get<Int>("remoteness")
//        set(value) {
//            field = value
//            handle.set("remoteness", value)
//        }

    val accessOptions: LiveData<Dictionary> = MutableLiveData(dictionariesManager.getAccessOptionDictionary())
    var access = handle.get<Int>("access")
        set(value) {
            field = value
            handle.set("access", value)
        }


    val fenceOptions: LiveData<Dictionary> = MutableLiveData(dictionariesManager.getFenceOptionsDictionary())
    var fence = handle.get<Int>("fence")
        set(value) {
            field = value
            handle.set("fence", value)
        }


    val coverageOptions: LiveData<Dictionary> = MutableLiveData(dictionariesManager.getCoverageOptionsDictionary())
    var coverage = handle.get<Int>("coverage")
        set(value) {
            field = value
            handle.set("coverage", value)
        }

    val kgoOptions: LiveData<Dictionary> = MutableLiveData(dictionariesManager.getKGOOptionsDictionary())
    var kgo = handle.get<Int>("kgo")
        set(value) {
            field = value
            handle.set("kgo", value)
        }

    private val _images = handle.getLiveData<MutableList<PhotoWrapper>>("images", mutableListOf())
    val images: LiveData<MutableList<PhotoWrapper>>
        get() = _images


    fun setEditData(data: ContainerAreaShortModel?) {
        if (data != null) {
            _isNewMode.value = false
            _id = data.id

            //MAP MODEL TO FIELDS
            area = data.area
            registryNumber = data.registryNumber
            data.photos?.let {
                _images.value = it.map { photo -> PhotoWrapper(photo) }.toMutableList()
            }
            data.location?.let {
                _address.value = AddressModel(it, "", data.coordinates?.lat, data.coordinates?.lng)
            }

        }
    }

    fun setAddress(addressModel: AddressModel) {
        _address.postValue(addressModel)
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

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(500)

            //Split images to old and new
            val oldPhotos = _images.value?.filter { it.uploaded != null }?.map { it.uploaded!! }
            val newPhotos = _images.value?.filter { it.new != null }?.map { it.new!! }

            //Mapping fields to model
            //TODO FIELD CHECKS

            val coordinatesModel = CoordinatesModel(
                    _address.value?.lng ?: 0.0,
                    _address.value?.lat ?: 0.0
            )

            val newModel = ContainerAreaShortModel(
                    _id,
                    area,
                    null,
                    coordinatesModel,
                    _address.value?.value,
                    registryNumber,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            )

            inventoryInteractor.updateContainer(newModel, oldPhotos, newPhotos)
                    .fold(::handleFailure) {
                        _isSaved.postValue(it)
                    }
        }
    }

    sealed class FieldState() {
        @Parcelize
        object Default : FieldState(), Parcelable

        @Parcelize
        object Error : FieldState(), Parcelable
    }

}