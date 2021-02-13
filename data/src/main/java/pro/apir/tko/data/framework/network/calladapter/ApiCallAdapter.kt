package pro.apir.tko.data.framework.network.calladapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * Created by antonsarmatin
 * Date: 13/02/2021
 * Project: tko
 */
class ApiCallAdapter<N : Any>(
        private val responseType: Type,
        private val responseTransformer: ApiResponseTransformer
) : CallAdapter<N, Call<ApiResult<N>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<N>): Call<ApiResult<N>> {
        return ApiCall(call, responseTransformer)
    }
}