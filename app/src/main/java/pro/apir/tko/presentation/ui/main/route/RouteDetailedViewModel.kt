package pro.apir.tko.presentation.ui.main.route

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    //Navigation

    //TODO CURRENT

    private var _currentStopPos = handle.get<Int>("currentStop")
        set(value) {
            Log.d("route","current point pos: $value")
            field = value
            val stopsCount = _routeStops.value?.size ?: 0
            if (value != null && value in 0 until stopsCount) {
                setStopData(value)
            } else {
                _currentStop.postValue(null)
            }
        }
    val currentStopPos: Int?
        get() = _currentStopPos

    private val _currentStop = MutableLiveData<RoutePointModel>()
    val currentStop: LiveData<RoutePointModel>
        get() = _currentStop


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
        viewModelScope.launch(Dispatchers.Main) {
            _routeSession.value = sessionModel
            _routeStops.value = sessionModel.points

            if (_currentStopPos == null) {
                _currentStopPos = 0
            }

            when (sessionModel.state) {
                RouteStateConstants.ROUTE_TYPE_DEFAULT -> {
                    _state.value = RouteState.Default

                }
                RouteStateConstants.ROUTE_TYPE_PENDING -> {
                    _state.value = RouteState.Pending
                }
                RouteStateConstants.ROUTE_TYPE_IN_PROGRESS -> {
                    _state.value = RouteState.InProgress
                }
            }

            //todo set pending to current if current null
        }
    }
    //Route Navigation

    fun nextStop() {
        val currentPos = _currentStopPos
        val pointCount = _routeStops.value?.size
        if (pointCount != null && currentPos != null) {

            _currentStopPos = if (currentPos + 1 < pointCount) {
                currentPos + 1
            } else {
                0
            }

        }

    }

    fun previousStop() {
        val currentPos = _currentStopPos
        val pointCount = _routeStops.value?.size
        if (pointCount != null && currentPos != null) {

            _currentStopPos = if (currentPos - 1 >= 0) {
                currentPos - 1
            } else {
                pointCount - 1
            }
        }

    }

    fun setStopPos(pos : Int){
        _currentStopPos = pos
    }

    private fun setStopData(pos: Int){
        _currentStop.postValue(_routeStops.value?.get(pos))
        //todo photo?
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