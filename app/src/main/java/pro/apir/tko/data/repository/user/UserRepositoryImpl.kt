package pro.apir.tko.data.repository.user

import pro.apir.tko.core.constant.KEY_USER_ID
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.core.functional.onRight
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.UserApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.UserModel
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val tokenManager: TokenManager,
                                             private val userApi: UserApi,
                                             private val preferencesManager: PreferencesManager) : UserRepository, BaseRepository(tokenManager) {

    override suspend fun getUser(): Either<Failure, UserModel> {
        return request({ userApi.getUser() }, { response -> response.toModel() })
    }

    override suspend fun getUserId(): Either<Failure, Int> {
        val cache = preferencesManager.getInt(KEY_USER_ID)
        return if (cache != -1) Either.Right(cache) else {
            val result = request({ userApi.getUser() }, { it.id })
            result.onRight {
                preferencesManager.saveInt(KEY_USER_ID, it)
            }
            result
        }
    }
}