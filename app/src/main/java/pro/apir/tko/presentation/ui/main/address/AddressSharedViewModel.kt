package pro.apir.tko.presentation.ui.main.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pro.apir.tko.domain.model.AddressModel

/**
 * Created by Антон Сарматин
 * Date: 23.01.2020
 * Project: tko-android
 */
class AddressSharedViewModel : ViewModel() {

    private val _address = MutableLiveData<AddressModel>()
    val address: LiveData<AddressModel>
        get() = _address

    internal fun setAddress(data: AddressModel){
        _address.postValue(data)
    }

}