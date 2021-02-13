package pro.apir.tko.data.framework.network.calladapter

import retrofit2.Response
import javax.inject.Inject

class ApiResponseTransformer @Inject constructor() {

    fun <N> transform(response: Response<N>): ApiResult<N> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error("Unknown error", response.code())
            }
        } else {
            val errorMessage = response.errorBody()?.string()
            val message = if (errorMessage.isNullOrEmpty()) {
                response.message()
            } else {
                errorMessage
            }
            ApiResult.Error(message ?: "Unknown error", response.code())
        }
    }

    fun <N> transform(error: Throwable): ApiResult<N> {
        return ApiResult.Error(error.message ?: "Unknown error", 0)
    }

}