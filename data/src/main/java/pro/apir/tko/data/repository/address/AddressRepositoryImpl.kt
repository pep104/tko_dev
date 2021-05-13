package pro.apir.tko.data.repository.address

import pro.apir.tko.core.data.Resource
import pro.apir.tko.data.framework.network.api.SuggestionApi
import pro.apir.tko.data.framework.network.api.SuggestionDetailedApi
import pro.apir.tko.data.framework.network.model.request.GeolocateRequest
import pro.apir.tko.data.framework.network.model.request.SuggestionRequest
import pro.apir.tko.data.framework.network.model.request.data.LocationRequestData
import pro.apir.tko.domain.manager.LocationManager
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel
import pro.apir.tko.domain.repository.address.AddressRepository
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val suggestionApi: SuggestionApi,
    private val suggestionDetailedApi: SuggestionDetailedApi,
    private val locationManager: LocationManager
) : AddressRepository {

    override suspend fun getAddressSuggestions(
        query: String,
        locationModel: LocationModel?
    ): Resource<List<AddressModel>> {
        val locationRequestData = if (locationModel != null) {
            val location = locationManager.getCurrentLocation()
            listOf(
                LocationRequestData(
                    lat = location.lat,
                    lon = location.lon
                )
            )
        } else null

        val request = SuggestionRequest(
            query = query,
            location = locationRequestData
        )

        return suggestionApi.getAddressSuggestions(request)
            .toResult { it.suggestions.map { it.toModel() } }
    }

    override suspend fun getAddressDetailed(query: String): Resource<List<AddressModel>> {
        val request = SuggestionRequest(
            query = query,
            location = null,
            count = 1
        )
        return suggestionDetailedApi.getAddressDetailed(request)
            .toResult { it.suggestions.map { it.toModel() } }
    }

    override suspend fun getAddressByLocation(
        locationModel: LocationModel,
        radius: Int
    ): Resource<List<AddressModel>> {
        val request = GeolocateRequest(
            lat = locationModel.lat,
            lon = locationModel.lon,
            radius = radius
        )

        return suggestionApi.getAddressByLocation(request)
            .toResult { it.suggestions.map { it.toModel() } }
    }
}