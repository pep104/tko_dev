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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IGeoPoint
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.extension.roundUpNearest
import pro.apir.tko.data.framework.manager.location.LocationManager
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.route.photo.RoutePhotoInteractor
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractor
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

    protected var _lastPosition = handle.get<IGeoPoint>("bbox")
        set(value) {
            handle.set("bbox", value)
            if (value != null) {
                locationManager.saveLastLocation(LocationModel(value.latitude, value.longitude))
            }
            field = value
        }

    val lastPosition: IGeoPoint?
        get() = _lastPosition


    //Route

    private val _routeSession = handle.getLiveData<RouteSessionModel>("routeSession")
    val routeSession: LiveData<RouteSessionModel>
        get() = _routeSession


    private val _state = handle.getLiveData<RouteState>("state")
    val state: LiveData<RouteState>
        get() = _state


    private var stopsLocationJob: Job? = null

    private val _routeStops = handle.getLiveData<List<RoutePointModel>>("routeStops")
    val routeStops: LiveData<List<RoutePointModel>>
        get() = _routeStops

    //Navigation

    private var currentStopLocationJob: Job? = null
    private var photoJob: Job? = null

    private var _currentStopPos = handle.get<Int>("currentStop")
        set(value) {
            field = value
            val stopsCount = _routeStops.value?.size ?: 0
            currentStopLocationJob?.cancel()
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

    private val _currentStopStickyCoordinates = MutableLiveData<CoordinatesModel>()
    val currentStopStickyCoordinates: LiveData<CoordinatesModel>
        get() = _currentStopStickyCoordinates

    private val _loadingStopCompletion = handle.getLiveData<Boolean>("loadingStopCompletion")
    val loadingStopCompletion: LiveData<Boolean>
        get() = _loadingStopCompletion

    private val _loadingTrackingCompletion = handle.getLiveData<Boolean>("loadingTrackingCompletion")
    val loadingTrackingCompletion: LiveData<Boolean>
        get() = _loadingTrackingCompletion

    private val _eventTrackingCompletion = MutableLiveData<Boolean>()
    val eventTrackingCompletion: LiveData<Boolean>
        get() = _eventTrackingCompletion

    //Location

    private var lastUserLocation = handle.get<LocationModel>("lastLocation")
        set(value) {
            field = value

            _routeStops.value?.let {
                updateStopsDistances(it)
            }

            _currentStop.value?.let {
                updateCurrentStopDistance(it)
            }

        }


    init {
        collectLocations()
    }

    fun init(route: RouteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_routeSession.value == null) {
                routeSessionInteractor.getSessionFromRoute(route).collect {
                    it.fold(::handleFailure, ::setData)
                }
            }
        }
    }

    //Btn Start/Resume
    fun startTracking() {
        if (_state.value == RouteState.Default || _state.value == RouteState.Pending)
            viewModelScope.launch {
                _loadingTrackingCompletion.postValue(true)
                _routeSession.value?.let { sessionModel ->
                    routeSessionInteractor.startSession(sessionModel).fold({
                        _loadingTrackingCompletion.postValue(false)
                        handleFailure(it)
                    }, {
                        _loadingTrackingCompletion.postValue(false)
                        setData(it)
                    })
                }
            }
    }

    fun finishTracking() {
        if (_state.value == RouteState.Completed) {
            viewModelScope.launch {
                _routeSession.value?.let {
                    _loadingTrackingCompletion.postValue(true)
                    routeSessionInteractor.finishSession(it).fold({
                        _loadingTrackingCompletion.postValue(false)
                        handleFailure(it)
                    }, {
                        _loadingTrackingCompletion.postValue(false)
                        _eventTrackingCompletion.postValue(true)
                    })
                }
            }
        }
    }

    private fun setData(sessionModel: RouteSessionModel) {
        viewModelScope.launch(Dispatchers.Main) {
            _routeSession.value = sessionModel
            _routeStops.value = sessionModel.points

            val stopPos = _currentStopPos
            if (stopPos == null) {
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
                RouteStateConstants.ROUTE_TYPE_COMPLETED -> {
                    _state.value = RouteState.Completed
                }
                RouteStateConstants.ROUTE_TYPE_START_DISABLED -> {
                    _state.value = RouteState.Disabled
                }
            }

            updateStopsDistances(sessionModel.points)
            _currentStop.value?.let {
                updateCurrentStopDistance(it)
            }

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
        setStopData(stop)
    }

    private fun setStopData(stop: RoutePointModel?) {
        currentStopLocationJob?.cancel()
        viewModelScope.launch(Dispatchers.Main) {
            _currentStop.value = stop
            _currentStopStickyCoordinates.value = stop?.coordinates
            _isFollowEnabled.value = false
            if (stop != null)
                updateCurrentStopDistance(stop)
        }
    }

    fun completePoint() {
        viewModelScope.launch {
            val session = _routeSession.value
            val completablePoint = _currentStop.value
            val completablePointId = _currentStop.value?.pointId

            if (session != null && completablePointId != null && completablePoint != null) {

                if (completablePoint.photos.size < 2) {
                    handleFailure(PhotosNotEnoughError())
                } else {
                    _loadingStopCompletion.postValue(true)
                    routeSessionInteractor.completePoint(session, completablePointId).fold(
                            {
                                _loadingStopCompletion.postValue(false)
                                handleFailure(it)
                            },
                            {
                                setData(it)
                                //Update current pos
                                val completedPos = it.points.first { it.pointId == completablePointId }
                                setStopData(completedPos)
                                _loadingStopCompletion.postValue(false)
                                Unit
                            })
                }

            } else {
                handleFailure(SessionError())
            }
        }
    }

    //PHOTOS

    fun addPhotos(filePaths: List<String>) {
        viewModelScope.launch {
            val session = _routeSession.value
            val currentStopId = _currentStop.value?.pointId
            if (session != null && currentStopId != null) {
                val res = routePhotosInteractor.createPhotos(session, filePaths, currentStopId)
                setData(res)
                refreshCurrent(res, currentStopId)
            }
        }
    }

    fun deletePhoto(photoModel: PhotoModel) {
        viewModelScope.launch {
            val session = _routeSession.value
            val currentStopId = _currentStop.value?.pointId
            if (session != null && currentStopId != null) {
                val res = routePhotosInteractor.deletePhoto(session, photoModel, currentStopId)
                refreshCurrent(res, currentStopId)
            }
        }
    }

    private fun refreshCurrent(sessionModel: RouteSessionModel, pointId: Long) {
        setData(sessionModel)
        val addedPos = sessionModel.points.first { it.pointId == pointId }
        setStopData(addedPos)
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

    sealed class RouteState {

        @Parcelize
        object Default : RouteState(), Parcelable

        @Parcelize
        object Pending : RouteState(), Parcelable

        @Parcelize
        object InProgress : RouteState(), Parcelable

        @Parcelize
        object Disabled : RouteState(), Parcelable

        @Parcelize
        object Completed : RouteState(), Parcelable

    }

    class SessionError : Failure.FeatureFailure()
    class PhotosNotEnoughError : Failure.FeatureFailure()

    //test
    //fixme extract this function
    private fun collectLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            locationManager.getLocationFlow().collect { location ->
                lastUserLocation = location
            }
        }
    }

    private fun updateStopsDistances(stops: List<RoutePointModel>) {
        stopsLocationJob?.cancel()
        stopsLocationJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d("location", "stops start")
            stops.let {
                val result = mapDistances(it, lastUserLocation)
                withContext(Dispatchers.Main) {
                    _routeStops.value = result
                }
            }
            Log.d("location", "stops end")
        }
    }

    private fun updateCurrentStopDistance(current: RoutePointModel) {
        currentStopLocationJob?.cancel()
        currentStopLocationJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d("location", "current start")
            current.let {
                if (it.coordinates != null) {
                    val dist = calcDistance(it.coordinates, lastUserLocation)

                    withContext(Dispatchers.Main) {
                        _currentStop.value = RoutePointModel(dist?.toInt()?.roundUpNearest(10), it)
                    }
                }
            }
            Log.d("location", "current end")
        }
    }


    private fun mapDistances(list: List<RoutePointModel>, userLocation: LocationModel?): List<RoutePointModel> {
        return if (userLocation != null)
            list.map {
                val pointCoordinates = it.coordinates
                if (pointCoordinates != null) {
                    val dist = calcDistance(pointCoordinates, userLocation)
                    RoutePointModel(dist?.toInt()?.roundUpNearest(10), it)
                } else {
                    it
                }
            }
        else
            list
    }

    private fun calcDistance(point: CoordinatesModel?, user: LocationModel?): Double? {
        return if (point != null && user != null) {
            calcDistance(user.lat, user.lon, point.lat, point.lng)
        } else {
            null
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