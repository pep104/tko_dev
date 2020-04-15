package pro.apir.tko.domain.interactors.address

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.map
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.domain.model.AddressModel
import javax.inject.Inject

class AddressInteractorImpl @Inject constructor(private val addressRepository: AddressRepository) : AddressInteractor {

    private val dispatcher = Dispatchers.IO

    override suspend fun getAddressSuggestions(query: String): Either<Failure, List<AddressModel>> = withContext(dispatcher) {
        addressRepository.getAddressSuggestions(query)
    }

    override suspend fun getAddressDetailed(query: String): Either<Failure, List<AddressModel>> = withContext(dispatcher) {
        addressRepository.getAddressDetailed(query)
    }

    override suspend fun getAddressDetailed(addressModel: AddressModel): Either<Failure, AddressModel> = withContext(dispatcher) {
        val detailedResult = addressRepository.getAddressDetailed(addressModel.value)
        detailedResult.map {
            if (it.isEmpty() || it[0].lng == null || it[0].lat == null)
                addressModel
            else {
                AddressModel(addressModel.value, addressModel.unrestrictedValue, it[0].lat, it[0].lng)
            }
        }
    }
}