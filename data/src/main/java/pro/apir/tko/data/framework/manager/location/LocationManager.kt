package pro.apir.tko.data.framework.manager.location

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.domain.model.LocationModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-13
 * Project: tko-android
 */
interface LocationManager {

    fun saveLastLocation(model: LocationModel)

    fun getLastLocation(): LocationModel?

    suspend fun getCurrentLocation(): LocationModel

    fun getLocationFlow(): Flow<LocationModel>

}