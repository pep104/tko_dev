package pro.apir.tko.data.repository

import retrofit2.Response
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.TokenManager

abstract class BaseRepository(private val tokenManager: TokenManager, private val tokenStrategy: TokenStrategy? = TokenStrategy.CHECK) {

    //Подумать над внедрением лишней зависимости,
    //мб есть вариант провернуть проверку истечения рефрештокена лучше,
    //чем внедряя менеджер в каждый класс наследующийся от базового репозитория

    protected suspend fun <T, R> request(call: Response<T>, transform: (T) -> R): Either<Failure, R> {
        return if (!tokenManager.isRefreshTokenExpired() || tokenStrategy == TokenStrategy.NO_AUTH) {
            try {
                val body = call.body()
                when (call.isSuccessful && body != null) {
                    true -> Either.Right(transform(body))
                    false -> Either.Left(Failure.ServerError)
                }
            } catch (exception: Throwable) {
                Either.Left(Failure.ServerError)
            }
        } else {
            Either.Left(Failure.RefreshTokenExpired)
        }
    }

    enum class TokenStrategy {
        CHECK, NO_AUTH
    }

}