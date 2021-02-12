package pro.apir.tko.data.repository.user

import pro.apir.tko.core.constant.KEY_USER_ID
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.api.UserApi
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.UserModel
import pro.apir.tko.domain.repository.user.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val credentialsManager: CredentialsManager,
                                             private val userApi: UserApi,
                                             private val preferencesManager: PreferencesManager) : UserRepository, BaseRepository(credentialsManager) {

    override suspend fun getUser(): Either<Failure, UserModel> {
        return request({ userApi.getUser() }, { response -> response.toModel() })
    }

    override suspend fun getUserId(): Int {
        return preferencesManager.getInt(KEY_USER_ID)
    }
}