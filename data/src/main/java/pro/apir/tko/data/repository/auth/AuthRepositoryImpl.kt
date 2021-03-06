package pro.apir.tko.data.repository.auth

import pro.apir.tko.core.data.Resource
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.repository.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi) : AuthRepository {

    override suspend fun auth(email: String, password: String): Resource<AuthTokenModel> {
        return authApi.auth(email, password).toResult { it.toModel() }
    }

}