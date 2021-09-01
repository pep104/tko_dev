package pro.apir.tko.presentation.ui.main

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pro.apir.tko.data.framework.manager.credentials.CredentialsManager
import pro.apir.tko.domain.interactors.blocked.BlockedInteractor
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 2019-12-28
 * Project: android-template
 */
@HiltViewModel
class GlobalState @Inject constructor(
    handle: SavedStateHandle,
    private val blockedInteractor: BlockedInteractor,
    private val credentialsManager: CredentialsManager,
) : ViewModel() {


    private val _userState = handle.getLiveData<UserState>("userState")
    val userState: LiveData<UserState>
        get() = Transformations.distinctUntilChanged(_userState)

    private val _menuState = handle.getLiveData<Boolean>("menuState", false)
    val menuState: LiveData<Boolean>
        get() = _menuState

    private val _blocked = handle.getLiveData<Boolean>("blocked", false)
    val blocked: LiveData<Boolean>
        get() = Transformations.distinctUntilChanged(_blocked)

    init {
        viewModelScope.launch {
            blockedInteractor.getBlock().collect {
                Log.d("GlobalState", "Is app blocked? -> $it")
                _blocked.postValue(it)
            }
        }
    }

    fun setUserState(state: UserState) {
        if (state == UserState.LoggedOut || state == UserState.TokenExpired)
            credentialsManager.onLogout(state == UserState.LoggedOut)

        _userState.postValue(state)
    }

    fun logOut() {
        setUserState(UserState.LoggedOut)
    }

    fun toggleMenu() {
        val current = _menuState.value
        if (current == false) {
            _menuState.postValue(true)
        } else {
            _menuState.postValue(false)
        }
    }

    fun closeMenu() {
        _menuState.postValue(false)
    }

    fun openMenu() {
        _menuState.postValue(true)
    }


    sealed class UserState() {
        @Parcelize
        object Authenticated : UserState(), Parcelable

        @Parcelize
        object LoggedOut : UserState(), Parcelable

        @Parcelize
        object LoginNeeded : UserState(), Parcelable

        @Parcelize
        object TokenExpired : UserState(), Parcelable
    }

}