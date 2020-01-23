package pro.apir.tko.presentation.ui.main.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pro.apir.tko.domain.model.SuggestionModel

/**
 * Created by Антон Сарматин
 * Date: 23.01.2020
 * Project: tko-android
 */
class AddressSharedViewModel : ViewModel() {

    private val _address = MutableLiveData<SuggestionModel>()
    val address: LiveData<SuggestionModel>
        get() = _address

    internal fun setAddress(data: SuggestionModel){
        _address.postValue(data)
    }

}