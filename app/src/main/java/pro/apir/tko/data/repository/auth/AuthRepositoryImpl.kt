package pro.apir.tko.data.repository.auth

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.model.RefreshTokenModel
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi) : AuthRepository, BaseRepository() {

    override suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel> = request(authApi.auth(email, password)) { it.toModel() }

    override suspend fun refresh(refresh: String): Either<Failure, RefreshTokenModel> = request(authApi.refresh(refresh)) { it.toModel() }

}