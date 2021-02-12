package pro.apir.tko.domain.interactors.auth

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.repository.auth.AuthRepository
import pro.apir.tko.domain.repository.credentials.CredentialsRepository
import pro.apir.tko.domain.repository.user.UserRepository
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(private val authRepository: AuthRepository,
                                             private val userRepository: UserRepository,
                                             private val credentialsRepository: CredentialsRepository) : AuthInteractor {

    override suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel> = withContext(Dispatchers.IO) {
        val result = authRepository.auth(email, password)
        if (result is Either.Right) {
            credentialsRepository.saveRefreshToken(result.b.refresh)
            credentialsRepository.saveAccessToken(result.b.access)
            val userResult = userRepository.getUser()
            when (userResult) {
                is Either.Left -> return@withContext Either.Left(userResult.a)
                is Either.Right -> {
                    Log.e("userId", userResult.b.id.toString())
                    credentialsRepository.saveUserId(userResult.b.id)
                }
            }
        }

        result
    }

}