package pro.apir.tko.presentation.ui.main.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import pro.apir.tko.core.data.onSuccess
import pro.apir.tko.core.utils.LocationUtils
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.domain.manager.LocationManager
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel
import pro.apir.tko.presentation.platform.BaseViewModel
import pro.apir.tko.presentation.utils.address.AddressSuggestionRequester
import pro.apir.tko.presentation.utils.geoPointFromLocationModel
import kotlin.math.absoluteValue

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class AddressViewModel @AssistedInject constructor(
    @Assisted
    private val handle: SavedStateHandle,
    private val addressRequester: AddressSuggestionRequester,
    private val locationManager: LocationManager,
) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<AddressViewModel>

    private var addressCoordinatesJob: Job? = null

    private val _address = handle.getLiveData<AddressModel>("address")
    val address: LiveData<AddressModel>
        get() = _address

    private val _suggestions = MutableLiveData<List<AddressModel>>()
    val suggestions: LiveData<List<AddressModel>>
        get() = _suggestions

    private val _addressCoordinatesChanger = MutableLiveData<AddressModel?>()
    val addressCoordinatesChanger: LiveData<AddressModel?>
        get() = _addressCoordinatesChanger

    private val _addressCoordinatesLoading = MutableLiveData<Boolean>(false)
    val addressCoordinatesLoading: LiveData<Boolean>
    get()  = _addressCoordinatesLoading

    private val _viewType = handle.getLiveData<ViewType>("viewType", ViewType.BOTTOM_CARD)
    val viewType: LiveData<ViewType>
        get() = _viewType

    //Coordinates edit

    private val _errorCoordinates = handle.getLiveData<Boolean>("errorCoordinates")
    val errorCoordinates: LiveData<Boolean>
        get() = _errorCoordinates

    //Map

    private val _isFollowEnabled = handle.getLiveData("isFollowEnabled", false)
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

        viewModelScope.launch {
            addressRequester.suggestions.collect { resource ->
                resource.onSuccess {
                    _suggestions.postValue(it)
                }
            }
        }

        viewModelScope.launch {
            addressRequester.selectedAddress.collect {
                when (it) {
                    is AddressSuggestionRequester.SuggestionResult.Final -> setFinalAddress(it.model)
                    is AddressSuggestionRequester.SuggestionResult.Partial -> setPartialAddress(it.model)
                }
            }
        }

    }

    //FUNCTIONS

    fun setViewType(type: ViewType) {
        setupForViewType(type)
        _viewType.postValue(type)
    }

    fun query(query: String) {
        addressRequester.query(query)
    }

    fun selectAddress(
        addressModel: AddressModel,
        forceSelection: Boolean = false,
        forceQuery: Boolean = false,
    ) {
        addressRequester.select(
            addressModel = addressModel,
            forceSelection = forceSelection,
            forceQuery = forceQuery
        )
    }

    fun selectUser() {
        addressRequester.fetchUser()
    }

    private fun setPartialAddress(addressModel: AddressModel) {
        _address.postValue(addressModel)
    }

    private fun setFinalAddress(addressModel: AddressModel) {
        _address.postValue(addressModel)
        _viewType.postValue(ViewType.BOTTOM_CARD)
    }

    //edit coordinates

    fun processInput(input: String) {
        viewModelScope.launch(Dispatchers.Default) {
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
    }

    fun updateCoordinatesOnDragEvent(lat: Double, lon: Double) {
        updateCoordinates(lat, lon)
    }

    fun submitCoordinatesUpdate() {
        _address.value = _addressCoordinatesChanger.value
    }

    private fun updateCoordinates(lat: Double, lon: Double) {
        addressCoordinatesJob?.cancel()
        addressCoordinatesJob = viewModelScope.launch {
            val currentAddress = _address.value
            if (currentAddress != null
                && !currentAddress.isUserLocation
                && checkDistanceThreshold(currentAddress, lat, lon)
            ) {
                updateCoordinatesForCurrentAddress(lat, lon)
            } else {
                updateCordinatesWithFetchNewAddress(lat, lon)
            }
        }
    }

    private fun updateCoordinatesForCurrentAddress(lat: Double, lon: Double) {
        val current = addressCoordinatesChanger.value
        if (current?.lat != lat || current.lng != lon)
            _addressCoordinatesChanger.value = current?.copy(lat = lat, lng = lon)
    }

    private suspend fun updateCordinatesWithFetchNewAddress(lat: Double, lon: Double) {
        _addressCoordinatesLoading.value = true
        delay(400)
        addressRequester.fetchByLocation(LocationModel(lat, lon)).fold(
            onFailure = {
                _addressCoordinatesLoading.value = false
                _addressCoordinatesChanger.postValue(null)
            },
            onSuccess = {
                _addressCoordinatesLoading.value = false
                _addressCoordinatesChanger.postValue(it.copy(lat = lat, lng = lon))
            }
        )
    }

    private fun checkDistanceThreshold(
        addressModel: AddressModel,
        lat: Double,
        lon: Double,
    ): Boolean {
        if (addressModel.lat == null || addressModel.lng == null) return false
        return LocationUtils.distanceBetween(addressModel.lat!!,
            addressModel.lng!!,
            lat,
            lon).absoluteValue <= FETCH_DISTANCE_THRESHOLD
    }

    //viewtype preconditions
    private fun setupForViewType(type: ViewType) {
        _addressCoordinatesChanger.value = null
        when (type) {
            ViewType.BOTTOM_CARD -> {

            }
            ViewType.SEARCH -> {

            }
            ViewType.LOCATION_COORDINATES -> {
                _addressCoordinatesChanger.value = _address.value
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


    override fun onCleared() {
        super.onCleared()
        addressRequester.destroy()
    }

    enum class ViewType {
        BOTTOM_CARD,
        SEARCH,
        LOCATION_COORDINATES
    }

    companion object {
        private const val FETCH_DISTANCE_THRESHOLD = 50
    }

}