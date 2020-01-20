package pro.apir.tko.presentation.ui.main.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.apir.tko.core.constant.KEY_ACCESS_TOKEN
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.ui.main.GlobalState
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val tokenManager: TokenManager) : BaseViewModel() {

    private val _state = MutableLiveData<GlobalState.UserState>()
    val state: LiveData<GlobalState.UserState>
        get() = _state

    init {

        viewModelScope.launch {
            delay(2000)
            if (tokenManager.getRefreshToken().isNotBlank() && tokenManager.getAccessToken().isNotBlank() && !tokenManager.isRefreshTokenExpired()) {
                _state.postValue(GlobalState.UserState.Authenticated)
            } else {
                _state.postValue(GlobalState.UserState.LoginNeeded)
            }

        }

    }

    private fun debug() {
        _state.postValue(GlobalState.UserState.LoginNeeded)
    }


}
