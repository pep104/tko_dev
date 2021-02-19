package pro.apir.tko.domain.interactors.address

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.map
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.repository.address.AddressRepository
import pro.apir.tko.domain.utils.substringLocationPrefix
import javax.inject.Inject

class AddressInteractorImpl @Inject constructor(private val addressRepository: AddressRepository) : AddressInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getAddressSuggestions(query: String): Resource<List<AddressModel>> = withContext(dispatcher) {
        addressRepository.getAddressSuggestions(query).map { list ->
            list.map {
                it.removeLocationPrefix()
            }
        }
    }

    override suspend fun getAddressDetailed(query: String): Resource<List<AddressModel>> = withContext(dispatcher) {
        addressRepository.getAddressDetailed(query).map { list ->
            list.map {
                it.removeLocationPrefix()
            }
        }
    }

    override suspend fun getAddressDetailed(addressModel: AddressModel): Resource<AddressModel> = withContext(dispatcher) {
        val detailedResult = addressRepository.getAddressDetailed(addressModel.value).map { list ->
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

    private fun AddressModel.removeLocationPrefix() = this.copy(value = this.value.substringLocationPrefix()
            ?: this.value, unrestrictedValue = this.unrestrictedValue.substringLocationPrefix()
            ?: this.unrestrictedValue)


}