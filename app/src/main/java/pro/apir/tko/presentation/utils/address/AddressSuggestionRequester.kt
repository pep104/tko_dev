package pro.apir.tko.presentation.utils.address

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.data.getOrElse
import pro.apir.tko.core.data.map
import pro.apir.tko.core.data.onSuccess
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 04/05/2021
 * Project: tko
 */
class AddressSuggestionRequester @Inject constructor(
    private val addressInteractor: AddressInteractor,
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var queryJob: Job? = null

    private var _query: Query = Query.empty()

    private val _suggestions = MutableSharedFlow<Resource<List<AddressModel>>>()
    val suggestions = _suggestions.asSharedFlow()

    private val _selectedAddress = MutableSharedFlow<SuggestionResult>()
    val selectedAddress = _selectedAddress.asSharedFlow()

    private var _userCoordinates: LocationModel? = null


    fun select(
        addressModel: AddressModel,
        forceSelection: Boolean = false,
        forceQuery: Boolean = false,
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
            fetchSuggestions(query.toQuery())
        }
    }

    suspend fun fetchByLocation(locationModel: LocationModel): Resource<AddressModel> {
        _userCoordinates = null
        return addressInteractor.getAddressByLocation(locationModel)
    }

    fun fetchUser() {
        scope.launch {
            addressInteractor.getAddressByUser().onSuccess {
                if (it.lat != null && it.lng != null)
                    _userCoordinates = LocationModel(it.lat!!, it.lng!!)
                setPartialResult(it)
            }
        }
    }

    private fun fetchSuggestions(query: Query) {
        if (query.get().length > QUERY_LENGTH_MIN_LIMIT) {
            queryJob?.cancel()
            queryJob = scope.launch {
                delay(DEBOUNCE_DELAY)
                val suggestionsResult = addressInteractor.getAddressSuggestions(query.get())
                    .map { list ->
                        list.map { address ->
                            address.setUserCoordinatesIfNeeded()
                        }
                    }
                _suggestions.emit(suggestionsResult)
            }
        }
    }

    private suspend fun fetchDetailed(addressModel: AddressModel): AddressModel {
        queryJob?.cancel()
        return addressInteractor.getAddressDetailed(addressModel).getOrElse(addressModel)
    }

    private suspend fun setPartialResult(addressModel: AddressModel) {
        _selectedAddress.emit(SuggestionResult.Partial(addressModel.setUserCoordinatesIfNeeded()))
        fetchSuggestions(addressModel.toQuery())
    }

    private suspend fun setFinalAddress(addressModel: AddressModel) {
        if (addressModel.lat == null || addressModel.lng == null) {
            setFinalAddress(fetchDetailed(addressModel))
        } else {
            _selectedAddress.emit(SuggestionResult.Final(addressModel.setUserCoordinatesIfNeeded()))
        }
    }

    private fun checkConfirmation(addressModel: AddressModel): Boolean {
        val current = _query
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

    private fun AddressModel.setUserCoordinatesIfNeeded(): AddressModel {
        val userCoordinates = _userCoordinates
        return if (userCoordinates != null) {
            this.copy(
                lat = userCoordinates.lat,
                lng = userCoordinates.lon
            )
        } else {
            this
        }
    }

    companion object {

        private const val DEBOUNCE_DELAY = 300L
        private const val QUERY_LENGTH_MIN_LIMIT = 2

    }

    sealed class Query {

        abstract fun get(): String

        data class Model(val addressModel: AddressModel) : Query() {
            override fun get() = addressModel.value
        }

        data class Text(val query: String) : Query() {
            override fun get() = query
        }

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