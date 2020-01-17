package pro.apir.tko.domain.interactors.auth

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.getOrElse
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.model.RefreshTokenModel
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(private val authRepository: AuthRepository) : AuthInteractor {
    override suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel> {
        return authRepository.auth(email, password)
    }

    override suspend fun refresh(refresh: String): Either<Failure, RefreshTokenModel> {
        return authRepository.refresh(refresh)
    }
}