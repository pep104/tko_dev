package pro.apir.tko.data.framework.network.api

import pro.apir.tko.data.framework.network.calladapter.ApiResult
import pro.apir.tko.data.framework.network.model.request.SuggestionRequest
import pro.apir.tko.data.framework.network.model.response.SuggestionResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by antonsarmatin
 * Date: 2020-01-24
 * Project: tko-android
 */
interface SuggestionDetailedApi {

    @POST("api/v2/suggest/address")
    suspend fun getAddressDetailed(@Body request: SuggestionRequest): ApiResult<SuggestionResponse>

}