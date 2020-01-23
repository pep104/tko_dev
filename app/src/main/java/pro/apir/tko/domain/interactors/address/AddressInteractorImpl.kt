package pro.apir.tko.domain.interactors.address

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.domain.model.SuggestionModel
import javax.inject.Inject

class AddressInteractorImpl @Inject constructor(private val addressRepository: AddressRepository) : AddressInteractor {

    override suspend fun getAddressSuggestions(query: String): Either<Failure, List<SuggestionModel>> {
        return addressRepository.getAddressSuggestions(query)
    }

}