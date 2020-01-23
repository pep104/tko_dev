package pro.apir.tko.data.repository.address

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.network.api.AddressApi
import pro.apir.tko.data.framework.network.model.request.SuggestionRequest
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.SuggestionModel
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(private val addressApi: AddressApi) : AddressRepository, BaseRepository(null, TokenStrategy.NO_AUTH) {

    override suspend fun getAddressSuggestions(query: String): Either<Failure, List<SuggestionModel>> {
        val request = SuggestionRequest(query, 5)
        return request({ addressApi.getAddressSuggestions(request) }, { it.suggestions.map { it.toModel() } })
    }

}