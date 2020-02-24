package pro.apir.tko.presentation.ui.main.route.detailed

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.route.RouteSessionInteractor
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteSessionModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
//TODO EXTRACT CONTROLS etc TO BASE DETAILED VM
class RouteDetailedViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                         private val routeSessionInteractor: RouteSessionInteractor) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<RouteDetailedViewModel>


    //Map

    private val _isFollowEnabled = handle.getLiveData<Boolean>("isFollowEnabled", false)
    val isFollowEnabled: LiveData<Boolean>
        get() = _isFollowEnabled

    protected var _zoomLevel = handle.get<Double>("zoomLevel")
        set(value) {
            handle.set("zoomLevel", value)
            field = value
        }

    val zoomLevel: Double?
        get() = _zoomLevel


    //Route

    private val _routeSession = handle.getLiveData<RouteSessionModel>("routeSession")
    val routeSession: LiveData<RouteSessionModel>
        get() = _routeSession


    private val _state = handle.getLiveData<RouteState>("state")
    val state: LiveData<RouteState>
        get() = _state


    private val _routeStops = handle.getLiveData<List<RoutePointModel>>("routeStops")
    val routeStops: LiveData<List<RoutePointModel>>
        get() = _routeStops


    fun init(route: RouteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_routeSession.value == null) {
                routeSessionInteractor.getInitialSessionFromRoute(route).fold(::handleFailure, ::setData)
            }
        }
    }

    //Btn Start/Resume
    fun start() {
        if (_state.value == RouteState.Default || _state.value == RouteState.Pending)
            viewModelScope.launch(Dispatchers.IO) {
                _routeSession.value?.let {
                    //TODO PROCEED STATE CHANGING
                    routeSessionInteractor.startSession(it).fold(::handleFailure, ::setData)
                }
            }

    }

    private fun setData(sessionModel: RouteSessionModel) {
        _routeSession.postValue(sessionModel)
        _routeStops.postValue(sessionModel.points)

        when (sessionModel.state) {
            RouteStateConstants.ROUTE_TYPE_DEFAULT -> {
                _state.postValue(RouteState.Default)
            }
            RouteStateConstants.ROUTE_TYPE_PENDING -> {
                _state.postValue(RouteState.Pending)
            }
            RouteStateConstants.ROUTE_TYPE_IN_PROGRESS -> {
                _state.postValue(RouteState.InProgress)
            }
        }

    }

    //controls

    //On some situations MapView disables follow, so we need to disable it in VM
    fun disableFollow() {
        if (_isFollowEnabled.value == true) {
            _isFollowEnabled.value = false
        }
    }

    fun switchFollow() {
        _isFollowEnabled.value?.let {
            _isFollowEnabled.value = !it
        }
    }

    fun setZoomLevel(zoomLevel: Double) {
        _zoomLevel = zoomLevel

    }

    //Screen Route State

    sealed class RouteState() {

        @Parcelize
        object Default : RouteState(), Parcelable

        @Parcelize
        object Pending : RouteState(), Parcelable

        @Parcelize
        object InProgress : RouteState(), Parcelable

    }

}