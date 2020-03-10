package pro.apir.tko.presentation.ui.main.route

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pro.apir.tko.core.extension.roundUpNearest
import pro.apir.tko.data.framework.manager.location.LocationManager
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.route.RouteSessionInteractor
import pro.apir.tko.domain.interactors.route.photo.RoutePhotoInteractor
import pro.apir.tko.domain.model.*
import pro.apir.tko.presentation.platform.BaseViewModel


/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
//TODO EXTRACT CONTROLS etc TO BASE DETAILED VM
class RouteDetailedViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                         private val routeSessionInteractor: RouteSessionInteractor,
                                                         private val routePhotosInteractor: RoutePhotoInteractor,
                                                         private val locationManager: LocationManager) : BaseViewModel() {

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

    private var currentStopLocationJob: Job? = null

    private var _currentStopPos = handle.get<Int>("currentStop")
        set(value) {
            field = value
            val stopsCount = _routeStops.value?.size ?: 0
            currentStopLocationJob?.cancel()
            if (value != null && value in 0 until stopsCount) {
                setStopData(value)
            } else {
                _currentStop.postValue(null)
                _currentStopPhotos.postValue(emptyList())
            }
        }
    val currentStopPos: Int?
        get() = _currentStopPos

    private val _currentStop = MutableLiveData<RoutePointModel>()
    val currentStop: LiveData<RoutePointModel>
        get() = _currentStop

//    private val _currentStopDistance = MutableLiveData<Double>()
//    val currentStopDistance: LiveData<Double>
//        get() = _currentStopDistance

    //TODO PHOTOS?
    private var photoJob: Job? = null

    private val _currentStopPhotos = MutableLiveData<List<PhotoModel>>()
    val currentStopPhotos: LiveData<List<PhotoModel>>
        get() = _currentStopPhotos

    init {
        collectLocations()
    }

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
        currentStopLocationJob?.cancel()

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
        currentStopLocationJob?.cancel()

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

    fun setStopPos(pos: Int) {
        _currentStopPos = pos
    }

    private fun setStopData(pos: Int) {
        val stop = _routeStops.value?.get(pos)
        _currentStop.postValue(stop)

        stop?.id?.let {
            photoJob?.cancel()
            kotlin.runCatching {
                photoJob = viewModelScope.launch {
                    _currentStopPhotos.postValue(routePhotosInteractor.getPhotos(it))
                }
            }
        }
    }

    //PHOTOS

    fun addPhotos(pathList: List<String>) {
        //TODO SAVE(CREATE) LOCAL PHOTOS AND ADD IT TO LD FIELD
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

    //test
    //fixme extract this function
    private fun collectLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            locationManager.getLocationFlow().collect { location ->
                val route = _routeStops.value
                val result = arrayListOf<RoutePointModel>()
                route?.forEach {
                    val locationRoutePoint = it.coordinates
                    if (locationRoutePoint != null) {
                        val dist = calcDistance(
                                location.lat,
                                location.lon,
                                it.coordinates.lat,
                                it.coordinates.lng
                        )
                        result.add(RoutePointModel(dist.toInt().roundUpNearest(10), it))
                    } else {
                        result.add(it)
                    }
                }
                _routeStops.postValue(result)

                val currentNavStop = _currentStop.value
                currentNavStop?.let { it ->
                    kotlin.runCatching {
                        currentStopLocationJob?.cancel()
                        currentStopLocationJob = viewModelScope.launch {
                            val locationRoutePoint = it.coordinates
                            if (locationRoutePoint != null) {
                                val dist = calcDistance(
                                        location.lat,
                                        location.lon,
                                        it.coordinates.lat,
                                        it.coordinates.lng
                                )
                                _currentStop.postValue(RoutePointModel(dist.toInt().roundUpNearest(10), it))
                            }
                        }
                    }
                }

            }
        }
    }

    //fixme extract this function
    private fun calcDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters

        //Not used
        val height: Double = 0.0

        distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)
        return Math.sqrt(distance)
    }

}