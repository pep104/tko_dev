package pro.apir.tko.presentation.ui.main.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.domain.interactors.host.HostInteractor
import pro.apir.tko.presentation.entities.HostUi
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.utils.mapper.HostMapper

/**
 * Created by antonsarmatin
 * Date: 2020-01-15
 * Project: android-template
 */
class LoginViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val authInteractor: AuthInteractor,
    private val hostInteractor: HostInteractor,
    private val hostMapper: HostMapper,
) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<LoginViewModel>

    private val _requestState = MutableLiveData<Boolean>()
    val requestState: LiveData<Boolean>
        get() = _requestState

    private val _host = MutableLiveData<HostUi>()
    val host: LiveData<HostUi>
        get() = _host

    init {
        getHost()
    }

    fun login(email: String, pass: String, host: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            updateHost(host)
            delay(300)
            authInteractor.auth(email, pass).fold(::handleFailure) {
                loading(false)
                _requestState.postValue(true)
            }
        }
    }

    private fun updateHost(name: String) {
        hostInteractor.save(hostMapper.map(name).toDomain())
        getHost()
    }

    private fun getHost() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentHost = HostUi.fromDomain(hostInteractor.getHost())
            _host.postValue(currentHost)
        }
    }

}