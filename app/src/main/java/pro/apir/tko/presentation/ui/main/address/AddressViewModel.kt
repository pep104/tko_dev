package pro.apir.tko.presentation.ui.main.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class AddressViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle, private val addressInteractor: AddressInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<AddressViewModel>

    //TODO VIEW STATE (SHOW, EDIT, LOCATION)

    private var queryJob: Job? = null

    private val _address = handle.getLiveData<AddressModel>("address")
    val address: LiveData<AddressModel>
        get() = _address

    private val _suggestions = MutableLiveData<List<AddressModel>>()
    val suggestions: LiveData<List<AddressModel>>
        get() = _suggestions

    fun query(query: String) {
        if (query.length > 3) {
            queryJob?.cancel()
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
                addressInteractor.getAddressSuggestions(query).fold(::handleFailure) {
                    _suggestions.postValue(it)
                }
            }
        }
    }

    fun setChoosed(addressModel: AddressModel) {
        if (addressModel.lat != null && addressModel.lng != null) {
            _address.postValue(addressModel)
        } else {
            fetchDetailed(addressModel)
        }
    }

    private fun fetchDetailed(addressModel: AddressModel) {
        queryJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            addressInteractor.getAddressDetailed(addressModel.value).fold({}, {
                if (it.isNotEmpty() && it.first().lat != null && it.first().lng != null) {
                    setChoosed(it.first())
                } else {
                    setChoosed(addressModel)
                }
            })
        }
    }

}