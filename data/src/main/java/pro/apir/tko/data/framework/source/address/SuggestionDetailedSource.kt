package pro.apir.tko.data.framework.source.address

import pro.apir.tko.data.framework.network.api.SuggestionDetailedApi
import pro.apir.tko.data.framework.network.model.request.SuggestionRequest
import pro.apir.tko.data.framework.network.model.response.SuggestionResponse
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by antonsarmatin
 * Date: 2020-01-24
 * Project: tko-android
 */
class SuggestionDetailedSource @Inject constructor(@Named("suggestionDetailed") retrofit: Retrofit) : SuggestionDetailedApi {

    private val api by lazy { retrofit.create(SuggestionDetailedApi::class.java) }


    override suspend fun getAddressDetailed(request: SuggestionRequest): Response<SuggestionResponse> {
        return api.getAddressDetailed(request)
    }
}