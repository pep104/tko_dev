package pro.apir.tko.presentation.platform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pro.apir.tko.core.exception.Failure

/**
 * Created by antonsarmatin
 * Date: 2020-01-29
 * Project: tko-android
 */
abstract class BaseViewModelExperimental : ViewModel() {

    abstract var viewState: MutableLiveData<out ViewState>

    var failure: MutableLiveData<Failure> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        this.failure.postValue(failure)
    }

    abstract class ViewState {

        var isLoading: Boolean = false

    }

}