package pro.apir.tko.data.framework.manager.location

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.Flow
import pro.apir.tko.domain.model.LocationModel
import javax.inject.Inject

class LocationManagerImpl @Inject constructor(private val context: Context) : LocationManager {

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

    override suspend fun getCurrentLocation(): LocationModel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocationFlow(): Flow<LocationModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}