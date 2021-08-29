package pro.apir.tko.data.framework.network.calladapter

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.exception.RefreshTokenNotValidException
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

            if (message.checkTokenError())
                ApiResult.Error(Failure.RefreshTokenNotValid(false))
            else
                ApiResult.Error(message ?: "Unknown error", response.code())
        }
    }

    fun <N> transform(error: Throwable): ApiResult<N> {

        return when {
            error is RefreshTokenNotValidException ->
                ApiResult.Error(Failure.RefreshTokenNotValid(false))
            error.message.checkTokenError() -> ApiResult.Error(Failure.RefreshTokenNotValid(false))
            else -> ApiResult.Error(error.message ?: "Unknown error", 0)
        }

    }

}