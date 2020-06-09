package pro.apir.tko.presentation.ui.main.address

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import pro.apir.tko.data.framework.manager.location.LocationManager
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.utils.geoPointFromLocationModel
import kotlin.system.measureNanoTime

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class AddressViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle,
                                                   private val addressInteractor: AddressInteractor,
                                                   private val locationManager: LocationManager) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<AddressViewModel>

    private var queryJob: Job? = null

    private var addressCoordinatesJob: Job? = null

    private val _address = handle.getLiveData<AddressModel>("address")
    val address: LiveData<AddressModel>
        get() = _address

    private val _suggestions = MutableLiveData<List<AddressModel>>()
    val suggestions: LiveData<List<AddressModel>>
        get() = _suggestions


    private val _viewType = handle.getLiveData<ViewType>("viewType", ViewType.BOTTOM_CARD)
    val viewType: LiveData<ViewType>
        get() = _viewType

    //Coordinates edit

    private val _errorCoordinates = handle.getLiveData<Boolean>("errorCoordinates")
    val errorCoordinates: LiveData<Boolean>
        get() = _errorCoordinates

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


    init {
        if (_lastPosition == null)
            viewModelScope.launch {
                _lastPosition = geoPointFromLocationModel(locationManager.getCurrentLocation())
            }
    }

    //FUNCTIONS

    fun setViewType(type: ViewType) {
        _viewType.postValue(type)
    }

    fun query(query: String) {
        if (query.length > 2) {
            queryJob?.cancel()
            viewModelScope.launch {
                delay(300)
                Log.e("http","query: $query")
                addressInteractor.getAddressSuggestions(query).fold(::handleFailure) {
                    _suggestions.postValue(it)
                }
            }
        }
    }

    fun setChoosed(addressModel: AddressModel) {
        if (addressModel.lat != null && addressModel.lng != null) {
           setAddress(addressModel)
        } else {
            fetchDetailedTest(addressModel)
        }
    }

    private fun fetchDetailed(addressModel: AddressModel) {
        queryJob?.cancel()
        viewModelScope.launch() {
            addressInteractor.getAddressDetailed(addressModel.value).fold({}, {
                if (it.isNotEmpty() && it.first().lat != null && it.first().lng != null) {
                    setAddress(it.first())
                } else {
                    //Зациклится же? Или так надо?
                    setChoosed(addressModel)
                }
            })
        }
    }

    private fun fetchDetailedTest(addressModel: AddressModel) {
        queryJob?.cancel()
        viewModelScope.launch {
            addressInteractor.getAddressDetailed(addressModel).fold({},{
                setAddress(it)
            })
        }
    }

    private fun setAddress(addressModel: AddressModel){
        _address.postValue(addressModel)
        _viewType.postValue(ViewType.BOTTOM_CARD)
    }

    //edit coordinates

    fun processInput(input: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val time = measureNanoTime {

                if (input.isNullOrBlank()) {
                    _errorCoordinates.postValue(false)
                } else {
                    val divider = ','
                    val dot = '.'
                    val inputSolid = input.replace(" ", "")
                    val coordinates = inputSolid.split(divider)
                    if (coordinates.size != 2) {
                        _errorCoordinates.postValue(true)
                    } else {
                        _errorCoordinates.postValue(false)
                        val latString = coordinates[0]
                        val lonString = coordinates[1]

                        val latDouble = latString.toDoubleOrNull()
                        val lonDouble = lonString.toDoubleOrNull()

                        if (latDouble != null && lonDouble != null) {
                            when {
                                latDouble in -90.0..90.0 && lonDouble in -180.0..180.0 -> {
                                    _errorCoordinates.postValue(false)
                                    updateCoordinates(latDouble, lonDouble)
                                }
                                else -> {
                                    _errorCoordinates.postValue(true)
                                }
                            }

                        } else {
                            _errorCoordinates.postValue(true)
                        }

                    }
                }


            }
            Log.e("mask","time: $time")
        }
    }

    fun updateCoordinatesOnDragEvent(lat: Double, lon: Double){
        updateCoordinates(lat, lon)
    }

    private fun updateCoordinates(lat: Double, lon: Double) {
        addressCoordinatesJob?.cancel()
        addressCoordinatesJob = viewModelScope.launch {
            delay(400)
            val addressModel = _address.value
            if (addressModel != null && (addressModel.lat != lat || addressModel.lng != lon)) {
                val newAddressModel = AddressModel(addressModel.value, addressModel.unrestrictedValue, lat, lon)
                _address.postValue(newAddressModel)
            }
        }
    }


    //controls

    //On some situations MapView disables follow, so we need to disable it in VM
    fun disableFollow() {
        if (_isFollowEnabled.value == true) {
            switchFollow()
        }
    }

    fun enableFollow() {
        if (_isFollowEnabled.value == false) {
            switchFollow()
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


    enum class ViewType {
        BOTTOM_CARD,
        SEARCH,
        LOCATION_COORDINATES
    }

}