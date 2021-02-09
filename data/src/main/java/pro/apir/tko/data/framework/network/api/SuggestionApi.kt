package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.model.request.SuggestionRequest
import pro.apir.tko.data.framework.network.model.response.SuggestionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
interface SuggestionApi {

    @POST("suggestions/api/4_1/rs/suggest/address")
    suspend fun getAddressSuggestions(@Body request: SuggestionRequest): Response<SuggestionResponse>


}