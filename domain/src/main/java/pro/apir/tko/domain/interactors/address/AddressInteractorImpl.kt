package pro.apir.tko.domain.interactors.address

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.asResource
import pro.apir.tko.core.data.map
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.utils.LocationUtils
import pro.apir.tko.domain.manager.LocationManager
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel
import pro.apir.tko.domain.repository.address.AddressRepository
import pro.apir.tko.domain.utils.substringLocationPrefix
import javax.inject.Inject

class AddressInteractorImpl @Inject constructor(
    private val addressRepository: AddressRepository,
    private val locationManager: LocationManager,
) : AddressInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getAddressSuggestions(
        query: String,
        useLocation: Boolean,
    ): Resource<List<AddressModel>> = withContext(dispatcher) {

        val addresses = addressRepository.getAddressSuggestions(query)

        val addressesByLocation = if (useLocation) {
            withTimeoutOrNull(1000) {
                val location = locationManager.getCurrentLocation()
                addressRepository.getAddressSuggestions(query, location)
            }
        } else null

        val resultList = arrayListOf<AddressModel>()

        addresses.fold(
            onFailure = {
                return@withContext addresses
            },
            onSuccess = {
                if (useLocation && addressesByLocation != null && addressesByLocation is Resource.Success) {
                    resultList.addAll(
                        mergeAddresses(it, addressesByLocation.data)
                    )
                } else {
                    resultList.addAll(it)
                }
            }
        )

        return@withContext Resource.Success(
            resultList
                .map { it.removeLocationPrefix() }
                .sortByDistance(locationManager.getLastLocation())
        )
    }

    private fun mergeAddresses(
        addresses: List<AddressModel>,
        locationBasedAddresses: List<AddressModel>,
    ): List<AddressModel> =
        arrayListOf<AddressModel>()
            .apply {
                addAll(addresses)
                addAll(locationBasedAddresses)
            }
            .distinct()

    private fun List<AddressModel>.sortByDistance(
        location: LocationModel?,
        isNearest: Boolean = true,
    ) =
        if (location != null)
            this.sortedBy {
                LocationUtils.distanceBetween(
                    lat1 = location.lat,
                    lon1 = location.lon,
                    lat2 = it.lat ?: 0.0,
                    lon2 = it.lng ?: 0.0
                )
            }
        else this


    override suspend fun getAddressDetailed(query: String): Resource<List<AddressModel>> =
        withContext(dispatcher) {
            addressRepository.getAddressDetailed(query).map { list ->
                list.map {
                    it.removeLocationPrefix()
                }
            }
        }

    override suspend fun getAddressDetailed(addressModel: AddressModel): Resource<AddressModel> =
        withContext(dispatcher) {
            val detailedResult =
                addressRepository.getAddressDetailed(addressModel.value).map { list ->
                    list.map {
                        it.removeLocationPrefix()
                    }
                }
            detailedResult.map {
                if (it.isEmpty() || it[0].lng == null || it[0].lat == null)
                    addressModel
                else {
                    addressModel.copy(lat = it[0].lat, lng = it[0].lng)
                }
            }
        }

    override suspend fun getAddressByLocation(locationModel: LocationModel): Resource<AddressModel> =
        withContext(dispatcher) {
            var failure: Failure? = null
            var finalResult: AddressModel? = null
            var fetchRadius = 20
            val fetchRadiusStep = 10
            val fetchRadiusThreshold = 100
            while (fetchRadius <= fetchRadiusThreshold
                && (finalResult == null || !finalResult.isContainsHouse)
                && failure == null
            ) {
                val fetchResult = addressRepository.getAddressByLocation(locationModel, fetchRadius)
                    .map { it.removeLocationPrefix() }
                if (fetchResult is Resource.Error) {
                    failure = fetchResult.failure
                    break
                }

                if (fetchResult is Resource.Success) {
                    if (fetchResult.data.isNotEmpty()) {
                        finalResult = fetchResult.data[0]
                    }

                    fetchRadius += fetchRadiusStep
                }
            }

            return@withContext if (finalResult != null) {
                Resource.Success(finalResult)
            } else {
                Resource.Error(failure ?: Failure.Ignore)
            }
        }

    override suspend fun getAddressByUser(): Resource<AddressModel> = withContext(dispatcher) {
        val userLocation = locationManager.getLastLocation() ?: locationManager.getCurrentLocation()
        val locations = getAddressByLocation(locationModel = userLocation)

        return@withContext when (locations) {
            is Resource.Error -> locations
            is Resource.Success -> {
                locations.data
                    .copy(isUserLocation = true)
                    .removeLocationPrefix()
                    .asResource()
            }
        }
    }

    private fun AddressModel.removeLocationPrefix() =
        this.copy(
            value = this.value.substringLocationPrefix() ?: this.value,
            unrestrictedValue = this.unrestrictedValue.substringLocationPrefix()
                ?: this.unrestrictedValue
        )

    private fun List<AddressModel>.removeLocationPrefix() =
        this.map { it.removeLocationPrefix() }

}