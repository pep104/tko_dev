package pro.apir.tko.presentation.ui.main.inventory.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class InventoryEditViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<InventoryEditViewModel>

    private val _isNewMode = handle.getLiveData<Boolean>("mode", true)
    val isNewMode: LiveData<Boolean>
        get() = _isNewMode

    fun setEditData(data: Any) {
        _isNewMode.value = false
    }

    //TODO FIELDS SAVE STATE

    //TODO PHOTOS

    //???

}