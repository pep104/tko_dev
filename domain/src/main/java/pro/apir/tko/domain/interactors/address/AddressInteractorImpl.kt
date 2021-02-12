package pro.apir.tko.domain.interactors.address

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.map
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.repository.address.AddressRepository
import pro.apir.tko.domain.utils.substringLocationPrefix
import javax.inject.Inject

class AddressInteractorImpl @Inject constructor(private val addressRepository: AddressRepository) : AddressInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getAddressSuggestions(query: String): Either<Failure, List<AddressModel>> = withContext(dispatcher) {
        addressRepository.getAddressSuggestions(query).map { list ->
            list.map {
                it.removeLocationPrefix()
            }
        }
    }

    override suspend fun getAddressDetailed(query: String): Either<Failure, List<AddressModel>> = withContext(dispatcher) {
        addressRepository.getAddressDetailed(query).map { list ->
            list.map {
                it.removeLocationPrefix()
            }
        }
    }

    override suspend fun getAddressDetailed(addressModel: AddressModel): Either<Failure, AddressModel> = withContext(dispatcher) {
        val detailedResult = addressRepository.getAddressDetailed(addressModel.value).map { list ->
            list.map {
                it.removeLocationPrefix()
            }
        }
        detailedResult.map {
            if (it.isEmpty() || it[0].lng == null || it[0].lat == null)
                addressModel
            else {
                AddressModel(addressModel.value, addressModel.unrestrictedValue, it[0].lat, it[0].lng)
            }
        }
    }

    private fun AddressModel.removeLocationPrefix() = this.copy(value = this.value.substringLocationPrefix()
            ?: this.value, unrestrictedValue = this.unrestrictedValue.substringLocationPrefix()
            ?: this.unrestrictedValue)


}