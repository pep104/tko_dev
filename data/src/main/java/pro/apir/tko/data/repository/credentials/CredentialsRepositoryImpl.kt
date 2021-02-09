package pro.apir.tko.data.repository.credentials

import pro.apir.tko.domain.repository.credentials.CredentialsRepository
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 09/02/2021
 * Project: tko
 */
class CredentialsRepositoryImpl @Inject constructor() : CredentialsRepository {

    override fun isRefreshTokenExpired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun saveRefreshToken(refreshToken: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun saveAccessToken(accessToken: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAccessToken(): String {
        TODO("Not yet implemented")
    }

    override fun getRefreshToken(): String {
        TODO("Not yet implemented")
    }

    override fun saveUserId(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getUserId(): Int {
        TODO("Not yet implemented")
    }
}