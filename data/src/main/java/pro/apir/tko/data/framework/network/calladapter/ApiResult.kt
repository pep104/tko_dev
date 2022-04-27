package pro.apir.tko.data.framework.network.calladapter

import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.exception.Failure

/**
 * Created by antonsarmatin
 * Date: 13/02/2021
 * Project: tko
 */
sealed class ApiResult<out N> {

    data class Success<out N>(val data: N) : ApiResult<N>()

    data class Error<out N>(val failure: Failure) : ApiResult<N>() {

        constructor(errorMessage: String, code: Int) : this(Failure.ServerError(errorMessage, code))

    }

    fun <R> toResult(transform: (N) -> R): Resource<R> {
        return when (this) {
            is Success -> Resource.Success(transform(this.data))
            is Error -> Resource.Error(this.failure)
        }
    }

}