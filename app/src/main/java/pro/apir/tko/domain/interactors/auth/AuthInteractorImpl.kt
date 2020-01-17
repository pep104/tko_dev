package pro.apir.tko.domain.interactors.auth

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.model.RefreshTokenModel
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(private val authRepository: AuthRepository) : AuthInteractor {
    override suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun refresh(refresh: String): Either<Failure, RefreshTokenModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}