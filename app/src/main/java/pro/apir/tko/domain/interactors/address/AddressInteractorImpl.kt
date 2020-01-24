package pro.apir.tko.domain.interactors.address

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.domain.model.AddressModel
import javax.inject.Inject

class AddressInteractorImpl @Inject constructor(private val addressRepository: AddressRepository) : AddressInteractor {

    override suspend fun getAddressSuggestions(query: String): Either<Failure, List<AddressModel>> {
        return addressRepository.getAddressSuggestions(query)
    }

    override suspend fun getAddressDetailed(query: String): Either<Failure, List<AddressModel>> {
        return addressRepository.getAddressDetailed(query)
    }

}