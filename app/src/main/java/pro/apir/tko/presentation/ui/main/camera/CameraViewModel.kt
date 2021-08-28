package pro.apir.tko.presentation.ui.main.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pro.apir.tko.presentation.extension.notifyObserver
import pro.apir.tko.presentation.platform.BaseViewModel
import java.io.File
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 26.01.2020
 * Project: tko-android
 */
//SAVE HERE?
@HiltViewModel
class CameraViewModel @Inject constructor(handle: SavedStateHandle) : BaseViewModel() {


    private val _photos = handle.getLiveData<MutableList<File>>("photos", mutableListOf())
    val photos: LiveData<MutableList<File>>
        get() = _photos


    fun addImage(photo: File) {
        _photos.value?.add(photo)
        _photos.notifyObserver()
    }

}