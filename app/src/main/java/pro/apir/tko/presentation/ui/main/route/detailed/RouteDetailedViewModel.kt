package pro.apir.tko.presentation.ui.main.route.detailed

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.route.RouteSessionInteractor
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.entities.RouteStop
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



    //TODO REPLACE WITH ROUTE SESSION MODEL!!!
    private val _state = handle.getLiveData<RouteState>("state")
    val state: LiveData<RouteState>
        get()  = _state

    //TODO REPLACE WITH ROUTE SESSION MODEL!!!
    //data
    private val _route = handle.getLiveData<RouteModel>("route")
    val route: LiveData<RouteModel>
        get() = _route

    //TODO REPLACE WITH ROUTE SESSION MODEL!!!
    //TEMP?
    private val _routeStops = handle.getLiveData<List<RouteStop>>("routeStops")
    val routeStops: LiveData<List<RouteStop>>
        get() = _routeStops


    fun init(route: RouteModel) {
        Log.e("route", "${_route.value?.name}")
        //TODO REPLACE WITH ROUTE SESSION MODEL!!!
        if (_route.value == null) {
            //TO MAP FUN?
            _route.postValue(route)
            _routeStops.postValue(route.stops.map { RouteStop(it) }.filter { it.stop.location != null })
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

    sealed class RouteState() {

        @Parcelize
        object Default: RouteState(), Parcelable

        @Parcelize
        object Pending: RouteState(), Parcelable

        @Parcelize
        object InProgress: RouteState(), Parcelable

    }

}