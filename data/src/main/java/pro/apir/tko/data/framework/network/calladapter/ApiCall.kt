package pro.apir.tko.data.framework.network.calladapter

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by antonsarmatin
 * Date: 13/02/2021
 * Project: tko
 */
class ApiCall<N : Any>(
        private val delegate: Call<N>,
        private val responseTransformer: ApiResponseTransformer
//    Можно интегрировать обработку ошибок для каждого ответа
//    private val errorConverter: Converter<ResponseBody, E>
) : Call<ApiResult<N>> {

    override fun enqueue(callback: Callback<ApiResult<N>>) {
        return delegate.enqueue(object : Callback<N> {
            override fun onResponse(call: Call<N>, response: Response<N>) {

                if (response.isSuccessful) {
                    callback.onResponse(
                            this@ApiCall,
                            Response.success(responseTransformer.transform(response))
                    )
                } else {
                    //Можно интегрировать обработку ошибок для ответа
                    callback.onResponse(
                            this@ApiCall,
                            Response.success(responseTransformer.transform(response))
                    )
                }
            }

            override fun onFailure(call: Call<N>, t: Throwable) {
                callback.onResponse(
                        this@ApiCall,
                        Response.success(responseTransformer.transform(t))
                )
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun clone() = ApiCall(delegate.clone(), responseTransformer)

    override fun isCanceled() = delegate.isCanceled

    override fun cancel() = delegate.cancel()

    override fun execute(): Response<ApiResult<N>> {
        throw UnsupportedOperationException("ApiCall doesn't support execute")
    }

    override fun request() = delegate.request()

}