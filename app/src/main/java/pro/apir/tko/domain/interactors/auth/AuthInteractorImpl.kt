package pro.apir.tko.domain.interactors.auth

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.constant.KEY_USER_ID
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.onRight
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.model.AuthTokenModel
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(private val authRepository: AuthRepository,
                                             private val userRepository: UserRepository,
                                             private val tokenManager: TokenManager,
                                             private val preferencesManager: PreferencesManager) : AuthInteractor {

    override suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel> = withContext(Dispatchers.IO) {
        val result = authRepository.auth(email, password)
        if (result is Either.Right) {
            tokenManager.saveRefreshToken(result.b.refresh)
            tokenManager.saveAccessToken(result.b.access)
            val userResult = userRepository.getUser()
            userResult.onRight {
                Log.e("userId", it.id.toString())
                preferencesManager.saveInt(KEY_USER_ID, it.id)
            }
        }

        result
    }

}