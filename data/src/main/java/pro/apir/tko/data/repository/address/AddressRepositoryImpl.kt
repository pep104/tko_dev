package pro.apir.tko.data.repository.address

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.network.api.SuggestionApi
import pro.apir.tko.data.framework.network.api.SuggestionDetailedApi
import pro.apir.tko.data.framework.network.model.request.SuggestionRequest
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.repository.address.AddressRepository
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(private val suggestionApi: SuggestionApi, private val suggestionDetailedApi: SuggestionDetailedApi) : AddressRepository, BaseRepository(null, TokenStrategy.NO_AUTH) {

    override suspend fun getAddressSuggestions(query: String): Either<Failure, List<AddressModel>> {
        val request = SuggestionRequest(query)
        return request({ suggestionApi.getAddressSuggestions(request) }, { it.suggestions.map { it.toModel() } })
    }

    override suspend fun getAddressDetailed(query: String): Either<Failure, List<AddressModel>> {
        val request = SuggestionRequest(query, 1)
        return request({ suggestionDetailedApi.getAddressDetailed(request) }, {it.suggestions.map { it.toModel() }})
    }
}