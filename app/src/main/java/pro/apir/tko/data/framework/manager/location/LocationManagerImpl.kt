package pro.apir.tko.data.framework.manager.location

import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.domain.model.LocationModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationManagerImpl @Inject constructor(private val context: Context, private val preferencesManager: PreferencesManager) : LocationManager {

    private val KEY_LON = "lllon"
    private val KEY_LAT = "lllat"

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val singleRequest by lazy {
        LocationRequest().apply {
            numUpdates = 1
            maxWaitTime = 2000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }
    private val flowRequest by lazy {
        LocationRequest().apply {
            maxWaitTime = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    override suspend fun getCurrentLocation(): LocationModel = suspendCoroutine { continuation ->

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                fusedLocationProviderClient.removeLocationUpdates(this)
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
                fusedLocationProviderClient.requestLocationUpdates(singleRequest, locationCallback, Looper.myLooper())
            }
        } catch (e: Exception) {
            Log.e("locationSource", "Exception: ${e.message}")
        }


    }


    override fun getLocationFlow(): Flow<LocationModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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