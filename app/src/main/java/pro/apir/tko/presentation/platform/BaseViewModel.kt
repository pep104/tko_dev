package pro.apir.tko.presentation.platform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pro.apir.tko.core.exception.Failure

/**
 * Base ViewModel class with default Failure handling.
 * @see ViewModel
 * @see Failure
 */
abstract class BaseViewModel : ViewModel() {

    var failure: MutableLiveData<Failure> = MutableLiveData()

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    protected fun handleFailure(failure: Failure) {
        loading(false)
        this.failure.value = failure
    }

    protected fun loading(isLoading: Boolean) {
        this._loading.postValue(isLoading)
    }
}