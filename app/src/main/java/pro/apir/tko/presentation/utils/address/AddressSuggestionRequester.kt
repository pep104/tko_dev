package pro.apir.tko.presentation.utils.address

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.getOrElse
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.model.AddressModel
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 04/05/2021
 * Project: tko
 */
//TODO FETCH BY GEO LOC
// при выбранном адресе использовать порог drag от адреса для возможности донастройки
class AddressSuggestionRequester @Inject constructor(
    private val addressInteractor: AddressInteractor,
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var queryJob: Job? = null

    private val _query = MutableStateFlow<Query>(Query.empty())

    private val _suggestions = MutableSharedFlow<Resource<List<AddressModel>>>()
    val suggestions = _suggestions.asSharedFlow()

    private val _selectedAddress = MutableSharedFlow<SuggestionResult>()
    val selectedAddress = _selectedAddress.asSharedFlow()


    init {
        scope.launch {
            _query.collect {
                when (it) {
                    is Query.Model -> fetchSuggestions(it.addressModel.value)
                    is Query.Text -> fetchSuggestions(it.query)
                }
            }
        }
    }


    fun select(
        addressModel: AddressModel,
        forceSelection: Boolean = false,
        forceQuery: Boolean = false
    ) {
        scope.launch {
            val isFinal = checkConfirmation(addressModel)
            when {
                forceQuery -> setPartialResult(addressModel)
                isFinal || forceSelection -> setFinalAddress(addressModel)
                else -> setPartialResult(addressModel)
            }
        }
    }

    fun query(query: String) {
        scope.launch {
            _query.emit(query.toQuery())
        }
    }

    private fun fetchSuggestions(query: String) {
        if (query.length > QUERY_LENGTH_MIN_LIMIT) {
            queryJob?.cancel()
            queryJob = scope.launch {
                delay(DEBOUNCE_DELAY)
                val suggestionsResult = addressInteractor.getAddressSuggestions(query)
                _suggestions.emit(suggestionsResult)
            }
        }
    }

    private suspend fun fetchDetailed(addressModel: AddressModel): AddressModel {
        queryJob?.cancel()
        return addressInteractor.getAddressDetailed(addressModel).getOrElse(addressModel)
    }

    private suspend fun setPartialResult(addressModel: AddressModel) {
        _selectedAddress.emit(SuggestionResult.Partial(addressModel))
        _query.emit(addressModel.toQuery())
    }

    private suspend fun setFinalAddress(addressModel: AddressModel) {
        if (addressModel.lat == null || addressModel.lng == null) {
            setFinalAddress(fetchDetailed(addressModel))
        } else {
            _selectedAddress.emit(SuggestionResult.Final(addressModel))
        }
    }

    private fun checkConfirmation(addressModel: AddressModel): Boolean {
        val current = _query.value
        return when {
            current is Query.Model && current.addressModel == addressModel -> {
                true
            }
            addressModel.isContainsHouse -> {
                true
            }
            else -> {
                false
            }
        }
    }

    fun destroy() {
        scope.cancel()
    }


    companion object {

        private const val DEBOUNCE_DELAY = 300L
        private const val QUERY_LENGTH_MIN_LIMIT = 2

    }

    sealed class Query {
        data class Model(val addressModel: AddressModel) : Query()
        data class Text(val query: String) : Query()

        companion object {
            fun empty() = Text("")
        }
    }

    sealed class SuggestionResult {
        data class Partial(val model: AddressModel) : SuggestionResult()
        data class Final(val model: AddressModel) : SuggestionResult()
    }

    private fun AddressModel.toQuery() = Query.Model(this)
    private fun String.toQuery() = Query.Text(this)

}