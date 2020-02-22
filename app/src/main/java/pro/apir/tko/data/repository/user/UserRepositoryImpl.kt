package pro.apir.tko.data.repository.user

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.UserApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.UserModel
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val tokenManager: TokenManager,
                                             private val userApi: UserApi) : UserRepository, BaseRepository(tokenManager) {

    override suspend fun getUser(): Either<Failure, UserModel> {
        return request({ userApi.getUser() }, { response -> response.toModel() })
    }

}