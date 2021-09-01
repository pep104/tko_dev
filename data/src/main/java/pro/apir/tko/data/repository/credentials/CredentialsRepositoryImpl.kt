package pro.apir.tko.data.repository.credentials

import pro.apir.tko.data.framework.manager.credentials.CredentialsManager
import pro.apir.tko.domain.repository.credentials.CredentialsRepository
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 09/02/2021
 * Project: tko
 */
class CredentialsRepositoryImpl @Inject constructor(private val credentialsManager: CredentialsManager) : CredentialsRepository {

    override fun isRefreshTokenExpired(): Boolean = credentialsManager.isRefreshTokenExpired()

    override fun saveRefreshToken(refreshToken: String): Boolean = credentialsManager.saveRefreshToken(refreshToken)

    override fun saveAccessToken(accessToken: String): Boolean = credentialsManager.saveAccessToken(accessToken)

    override fun getAccessToken(): String = credentialsManager.getAccessToken()

    override fun getRefreshToken(): String = credentialsManager.getRefreshToken()

    override fun saveUserId(id: Int) = credentialsManager.saveUserId(id)

    override fun getUserId(): Int = credentialsManager.getUserId()

    override fun saveCredentials(login: String, password: String) = credentialsManager.saveCredentials(login, password)

    override fun getCredentials(): Pair<String, String> = credentialsManager.getCredentials()
}