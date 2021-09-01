package pro.apir.tko.data.framework.manager.credentials

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface CredentialsManager {

    fun onLogout(): Boolean
    
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