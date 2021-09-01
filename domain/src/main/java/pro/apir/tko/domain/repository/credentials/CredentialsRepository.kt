package pro.apir.tko.domain.repository.credentials

/**
 * Created by antonsarmatin
 * Date: 09/02/2021
 * Project: tko
 */
interface CredentialsRepository {


    fun isRefreshTokenExpired(): Boolean

    fun saveRefreshToken(refreshToken: String): Boolean

    fun saveAccessToken(accessToken: String): Boolean

    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun saveUserId(id: Int)

    fun getUserId(): Int

    fun saveCredentials(login: String, password: String)

    fun getCredentials(): Pair<String, String>


}