package pro.apir.tko.presentation.ui.main

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import pro.apir.tko.di.ViewModelAssistedFactory

/**
 * Created by antonsarmatin
 * Date: 2019-12-28
 * Project: android-template
 */
class GlobalState @AssistedInject constructor(@Assisted handle: SavedStateHandle) : ViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<GlobalState>

    private val _userState = handle.getLiveData<UserState>("userState")
    val userState: LiveData<UserState>
        get() = _userState

    fun setUserState(state: UserState) {
        _userState.postValue(state)
    }

    sealed class UserState() {
        @Parcelize
        object Authenticated : UserState(), Parcelable

        @Parcelize
        object LoginNeeded : UserState(), Parcelable

        @Parcelize
        object TokenExpired: UserState(), Parcelable
    }

}