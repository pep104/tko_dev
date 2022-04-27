package pro.apir.tko.presentation.ui.main.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.domain.interactors.host.HostInteractor
import pro.apir.tko.presentation.entities.HostUi
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.utils.mapper.HostMapper
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 2020-01-15
 * Project: android-template
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val authInteractor: AuthInteractor,
    private val hostInteractor: HostInteractor,
    private val hostMapper: HostMapper,
) : BaseViewModel() {


    private val _requestState = MutableLiveData<Boolean>()
    val requestState: LiveData<Boolean>
        get() = _requestState

    private val _host = MutableLiveData<HostUi>()
    val host: LiveData<HostUi>
        get() = _host

    private val _hasCredentials = MutableLiveData<CredentialsState>(CredentialsState.Empty)
    val hasCredentials: LiveData<CredentialsState>
        get() = _hasCredentials

    init {
        getHost()
        getCredentials()
    }

    fun login(email: String, pass: String, host: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            updateHost(host)
            delay(400)
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

    private fun getCredentials() {
        viewModelScope.launch(Dispatchers.IO) {
            val (email, pass) = authInteractor.getCredentials()
            if (email.isNotBlank() && pass.isNotBlank()) {
                _hasCredentials.postValue(
                    CredentialsState.Exists(email, pass)
                )
            }
        }
    }

    sealed class CredentialsState {
        object Empty : CredentialsState()
        data class Exists(val email: String, val pass: String) : CredentialsState()
    }

}