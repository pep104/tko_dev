package pro.apir.tko.presentation.ui.main.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.model.SuggestionModel
import pro.apir.tko.presentation.entities.AddressEntity
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

    private val _address = handle.getLiveData<AddressEntity>("address")
    val address: LiveData<AddressEntity>
        get() = _address

    private val _suggestions = MutableLiveData<List<SuggestionModel>>()
    val suggestions: LiveData<List<SuggestionModel>>
        get() = _suggestions

    fun query(query: String) {
        if (query.length > 3) {
            queryJob?.cancel()
            viewModelScope.launch(Dispatchers.IO) {
                addressInteractor.getAddressSuggestions(query).fold(::handleFailure) {
                    _suggestions.postValue(it)
                }
            }
        }
    }

}