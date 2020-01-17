package pro.apir.tko.presentation.ui.main.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-15
 * Project: android-template
 */
class LoginViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle, private val authInteractor: AuthInteractor) : BaseViewModel(){

    @AssistedInject.Factory
    interface Factory: ViewModelAssistedFactory<LoginViewModel>

    fun login(email: String, pass: String){
        //todo
       viewModelScope.launch {
           authInteractor.auth(email, pass)
       }

    }

}