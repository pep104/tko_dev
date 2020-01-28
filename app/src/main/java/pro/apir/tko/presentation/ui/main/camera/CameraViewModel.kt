package pro.apir.tko.presentation.ui.main.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.presentation.extension.notifyObserver
import pro.apir.tko.presentation.platform.BaseViewModel
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 26.01.2020
 * Project: tko-android
 */
//SAVE HERE?
class CameraViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<CameraViewModel>

    private val _photos = handle.getLiveData<MutableList<File>>("photos", mutableListOf())
    val photos: LiveData<MutableList<File>>
        get() = _photos


    fun addImage(photo: File){
        _photos.value?.add(photo)
        _photos.notifyObserver()
    }

}