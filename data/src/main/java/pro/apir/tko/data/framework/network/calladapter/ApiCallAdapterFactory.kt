package pro.apir.tko.data.framework.network.calladapter

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by antonsarmatin
 * Date: 13/02/2021
 * Project: tko
 */
class ApiCallAdapterFactory private constructor(private val transformer: ApiResponseTransformer) :
        CallAdapter.Factory() {

    override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        return try {
            // suspend functions wrap the response type in `Call`
            if (Call::class.java != getRawType(returnType)) {
                return null
            }

            // check first that the return type is `ParameterizedType`
            check(returnType is ParameterizedType) {
                "return type must be parameterized as Call<ApiResult<<Foo>> or Call<ApiResult<out Foo>>"
            }

            // get the response type inside the `Call` type
            val responseType = getParameterUpperBound(0, returnType)
            // if the response type is not ApiResponse then we can't handle this type, so we return null
            if (getRawType(responseType) != ApiResult::class.java) {
                return null
            }
            // the response type is ApiResult and should be parameterized
            check(responseType is ParameterizedType) { "Response must be parameterized as ApiResult<Foo> or ApiResult<out Foo>" }

            val successBodyType = getParameterUpperBound(0, responseType)
//            val errorBodyType = getParameterUpperBound(1, responseType)

            return ApiCallAdapter<Any>(successBodyType, transformer)
        } catch (exception: ClassCastException) {
            null
        }
    }

    companion object {
        @JvmStatic
        fun create(transformer: ApiResponseTransformer) = ApiCallAdapterFactory(transformer)
    }

}