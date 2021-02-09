package pro.apir.tko.data.framework.manager.token

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
interface CredentialsManager {
    
    fun isRefreshTokenExpired(): Boolean

    fun saveRefreshToken(refreshToken: String): Boolean

    fun saveAccessToken(accessToken: String): Boolean

    fun getAccessToken(): String

    fun getRefreshToken(): String
    
}