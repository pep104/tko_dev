package pro.apir.tko.presentation.ui.main.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.presentation.entities.AddressEntity
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class AddressViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<AddressViewModel>

    private val _address = handle.getLiveData<AddressEntity>("address")
    val address: LiveData<AddressEntity>
        get() = _address

}