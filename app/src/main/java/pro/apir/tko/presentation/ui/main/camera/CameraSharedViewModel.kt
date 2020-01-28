package pro.apir.tko.presentation.ui.main.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pro.apir.tko.presentation.platform.BaseSharedViewModel
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 26.01.2020
 * Project: tko-android
 */
class CameraSharedViewModel : BaseSharedViewModel() {

    private val _photos = MutableLiveData<List<File>>()
    val photos: LiveData<List<File>>
        get() = _photos

    fun setImages(list: List<File>) {
        _photos.postValue(list)
    }

    override fun consume() {
        _photos.postValue(null)
    }

}