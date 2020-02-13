package pro.apir.tko.presentation.ui.main.route.detailed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.entities.RouteStop
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
class RouteDetailedViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<RouteDetailedViewModel>

    private val _route = handle.getLiveData<RouteModel>("route")
    val route: LiveData<RouteModel>
        get() = _route


    //TEMP?
    private val _routeStops = handle.getLiveData<List<RouteStop>>("routeStops")
    val routeStops: LiveData<List<RouteStop>>
        get()  = _routeStops

    //TODO ROUTE SESSION

    fun init(route: RouteModel) {
        Log.e("route","${_route.value?.name}")
        if (_route.value == null) {
            //TO MAP FUN?
            _route.postValue(route)
        }

        _routeStops.postValue(route.stops.map { RouteStop(it) })
    }


}