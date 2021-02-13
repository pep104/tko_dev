package pro.apir.tko.data.repository.auth

import pro.apir.tko.core.data.Resource
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.repository.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi, private val credentialsManager: CredentialsManager) : AuthRepository, BaseRepository(credentialsManager, TokenStrategy.NO_AUTH) {

    override suspend fun auth(email: String, password: String): Resource<AuthTokenModel> {
        val result = request({ authApi.auth(email, password) }, { it.toModel() })

        return result
    }

}