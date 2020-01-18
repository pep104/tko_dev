package pro.apir.tko.presentation.ui.main.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.apir.tko.core.constant.KEY_ACCESS_TOKEN
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.ui.main.GlobalState
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val preferencesManager: PreferencesManager) : BaseViewModel() {

    private val _state = MutableLiveData<GlobalState.UserState>()
    val state: LiveData<GlobalState.UserState>
        get() = _state

    //TODO Вынести логику
    init {

        val aToken = preferencesManager.getString(KEY_ACCESS_TOKEN)
        if (aToken.isNotBlank()) {
            //TODO CHECK EXPIRATION
            //TODO REAUTH
            //TODO ???
        } else {
            //TODO LOGIN NEEDED
        }

        //TODO REMOVE
        viewModelScope.launch {
            delay(2000)
            _state.postValue(GlobalState.UserState.LoginNeeded)
        }

    }


    sealed class Navigate {
        object Main : Navigate()
        object Login : Navigate()
    }

}
