package pro.apir.tko.data.framework.source.address

import pro.apir.tko.data.framework.network.api.SuggestionApi
import pro.apir.tko.data.framework.network.model.request.SuggestionRequest
import pro.apir.tko.data.framework.network.model.response.SuggestionResponse
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
class SuggestionSource @Inject constructor(@Named("suggestion") suggestion: Retrofit ): SuggestionApi{

    private val api by lazy { suggestion.create(SuggestionApi::class.java) }


    override suspend fun getAddressSuggestions(request: SuggestionRequest): Response<SuggestionResponse> {
        return api.getAddressSuggestions(request)
    }


}