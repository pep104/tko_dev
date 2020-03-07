package pro.apir.tko.presentation.ui.main.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.sarmatin.mobble.utils.consumablelivedata.ConsumableLiveData
import ru.sarmatin.mobble.utils.consumablelivedata.ConsumableValue
import ru.sarmatin.mobble.utils.consumablelivedata.postValue
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
class CameraSharedViewModel2 : ViewModel() {

    private val _photoPathList = ConsumableLiveData<List<String>>()
    val photoPathList: LiveData<ConsumableValue<List<String>>>
        get() = _photoPathList

    fun setData(files: List<File>) {
        _photoPathList.postValue(files.map { it.absolutePath })
    }

}