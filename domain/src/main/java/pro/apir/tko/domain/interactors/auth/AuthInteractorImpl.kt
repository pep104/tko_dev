package pro.apir.tko.domain.interactors.auth

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.repository.auth.AuthRepository
import pro.apir.tko.domain.repository.credentials.CredentialsRepository
import pro.apir.tko.domain.repository.user.UserRepository
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository,
) : AuthInteractor {

    override suspend fun auth(email: String, password: String): Resource<AuthTokenModel> =
        withContext(Dispatchers.IO) {
            val result = authRepository.auth(email, password)
            if (result is Resource.Success) {
                credentialsRepository.saveRefreshToken(result.data.refresh)
                credentialsRepository.saveAccessToken(result.data.access)
                val userResult = userRepository.getUser()
                when (userResult) {
                    is Resource.Error -> return@withContext Resource.Error(userResult.failure)
                    is Resource.Success -> {
                        Log.e("userId", userResult.data.id.toString())
                        credentialsRepository.saveUserId(userResult.data.id)
                        credentialsRepository.saveCredentials(email, password)
                    }
                }
            }

            result
        }



    override fun getCredentials(): Pair<String, String>{
        return credentialsRepository.getCredentials()
    }
}