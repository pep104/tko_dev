package pro.apir.tko.data.repository

import retrofit2.Response
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either

abstract class BaseRepository {

    protected suspend fun <T, R> request(call: Response<T>, transform: (T) -> R): Either<Failure, R> {
        return try {
            val response = call
            val body = response.body()
            when (response.isSuccessful && body != null) {
                true -> Either.Right(transform(body))
                false -> Either.Left(Failure.ServerError)
            }
        } catch (exception: Throwable) {
            Either.Left(Failure.ServerError)
        }

    }

}