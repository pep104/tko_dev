package pro.apir.tko.data.framework.manager.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import pro.apir.tko.data.BuildConfig
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.domain.model.LocationModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationManagerImpl @Inject constructor(private val context: Context, private val preferencesManager: PreferencesManager) : LocationManager {

    private val KEY_LON = "lllon"
    private val KEY_LAT = "lllat"

    private val singleRequest by lazy {
        LocationRequest().apply {
            numUpdates = 1
            maxWaitTime = 2000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    private val flowRequest by lazy {
        LocationRequest().apply {
            maxWaitTime = 4500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 15000
            fastestInterval = 14000
            if (!BuildConfig.DEBUG) {
                smallestDisplacement = 50f
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationModel = suspendCoroutine { continuation ->

        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                locationClient.removeLocationUpdates(this)
                result?.let { locationResult ->
                    val location = locationResult.lastLocation
                    location?.let {
                        val locationModel = LocationModel(lat = it.latitude, lon = it.longitude)
                        saveLastLocation(locationModel)
                        continuation.resume(locationModel)
                    }
                }
            }
        }

        try {
            CoroutineScope(Dispatchers.Main).launch {
                locationClient.requestLocationUpdates(singleRequest, locationCallback, Looper.myLooper())
            }
        } catch (e: Exception) {
            Log.e("locationSource", "Exception: ${e.message}")
        }

    }


    @SuppressLint("MissingPermission")
    @ExperimentalCoroutinesApi
    override fun getLocationFlow(): Flow<LocationModel> = channelFlow {

        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                result?.lastLocation?.let {
                    offer(LocationModel(lat = it.latitude, lon = it.longitude))
                }
            }
        }

        locationClient.lastLocation.addOnSuccessListener {location ->
            location?.let { offer(LocationModel(lat = it.latitude, lon = it.longitude)) }
        }
        withContext(Dispatchers.Main) {
            locationClient.requestLocationUpdates(flowRequest, locationCallback, Looper.myLooper())
        }
        awaitClose {
            locationClient.removeLocationUpdates(locationCallback)
        }

    }

    override fun saveLastLocation(model: LocationModel) {
        preferencesManager.saveDouble(KEY_LAT, model.lat)
        preferencesManager.saveDouble(KEY_LON, model.lon)
    }

    override fun getLastLocation(): LocationModel? {
        val lat = preferencesManager.getDouble(KEY_LAT)
        val lon = preferencesManager.getDouble(KEY_LON)
        return if (preferencesManager.isExists(KEY_LAT) && preferencesManager.isExists(KEY_LON)) {
            LocationModel(lat, lon)
        } else {
            null
        }
    }
}