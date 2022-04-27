package pro.apir.tko.presentation.platform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import pro.apir.tko.core.exception.Failure

/**
 * Base ViewModel class with default Failure handling.
 * @see ViewModel
 * @see Failure
 */
abstract class BaseViewModel(handle: SavedStateHandle? = null) : ViewModel() {

    var failure: MutableLiveData<Failure> = MutableLiveData()

    private val _loading: MutableLiveData<Boolean> = handle?.getLiveData("loading") ?: MutableLiveData()
    val loading: LiveData<Boolean>
        get() = _loading

    protected fun handleFailure(failure: Failure) {
        loading(false)
        this.failure.postValue(failure)
    }

    protected fun loading(isLoading: Boolean) {
        this._loading.postValue(isLoading)
    }
}