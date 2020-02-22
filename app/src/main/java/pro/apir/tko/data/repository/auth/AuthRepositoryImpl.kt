package pro.apir.tko.data.repository.auth

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.AuthTokenModel
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi, private val tokenManager: TokenManager) : AuthRepository, BaseRepository(tokenManager, TokenStrategy.NO_AUTH) {

    override suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel> {
        val result = request({ authApi.auth(email, password) }, { it.toModel() })

        return result
    }

}