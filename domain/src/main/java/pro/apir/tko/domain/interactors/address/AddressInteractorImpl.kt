package pro.apir.tko.domain.interactors.address

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.map
import pro.apir.tko.core.utils.LocationUtils
import pro.apir.tko.domain.manager.LocationManager
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel
import pro.apir.tko.domain.repository.address.AddressRepository
import pro.apir.tko.domain.utils.substringLocationPrefix
import javax.inject.Inject

class AddressInteractorImpl @Inject constructor(
    private val addressRepository: AddressRepository,
    private val locationManager: LocationManager
) : AddressInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getAddressSuggestions(
        query: String,
        useLocation: Boolean
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
        locationBasedAddresses: List<AddressModel>
    ): List<AddressModel> =
        arrayListOf<AddressModel>()
            .apply {
                addAll(addresses)
                addAll(locationBasedAddresses)
            }
            .distinct()

    private fun List<AddressModel>.sortByDistance(
        location: LocationModel?,
        isNearest: Boolean = true
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

    override suspend fun getAddressByLocation(locationModel: LocationModel): Resource<List<AddressModel>>  = withContext(dispatcher) {
        //TODO increase radius if result is empty or level is not proper?
        addressRepository.getAddressByLocation(locationModel, 10)
    }

    private fun AddressModel.removeLocationPrefix() =
        this.copy(
            value = this.value.substringLocationPrefix() ?: this.value,
            unrestrictedValue = this.unrestrictedValue.substringLocationPrefix()
                ?: this.unrestrictedValue
        )


}