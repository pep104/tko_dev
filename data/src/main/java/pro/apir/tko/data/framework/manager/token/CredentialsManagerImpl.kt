package pro.apir.tko.data.framework.manager.token

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import pro.apir.tko.core.constant.KEY_ACCESS_TOKEN
import pro.apir.tko.core.constant.KEY_REFRESH_TOKEN
import pro.apir.tko.core.constant.KEY_REFRESH_TOKEN_EXPIRATION
import pro.apir.tko.core.constant.KEY_USER_ID
import pro.apir.tko.core.constant.extension.fromBase64
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import java.util.*
import javax.inject.Inject

class CredentialsManagerImpl @Inject constructor(private val preferencesManager: PreferencesManager) :
    CredentialsManager {

    override fun onLogout(): Boolean {
        saveUserId(-1)
        saveAccessToken("")
        saveRefreshToken("")
        return true
    }

    override fun isRefreshTokenExpired(): Boolean {
        //FIXME
        val currentTime =
            (Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis / 1000L) + 10800
        val refreshExpirationTime = preferencesManager.getLong(KEY_REFRESH_TOKEN_EXPIRATION)
        return refreshExpirationTime < currentTime
    }

    override fun saveRefreshToken(refreshToken: String): Boolean {
        preferencesManager.saveString(KEY_REFRESH_TOKEN, refreshToken)
        if (refreshToken.isNotBlank())
            retrieveExpiration(refreshToken)
        else
            preferencesManager.saveLong(KEY_REFRESH_TOKEN_EXPIRATION, 0)

        return true
    }

    override fun saveAccessToken(accessToken: String): Boolean {
        preferencesManager.saveString(KEY_ACCESS_TOKEN, accessToken)
        return true
    }

    override fun getAccessToken(): String {
        return preferencesManager.getString(KEY_ACCESS_TOKEN)
    }

    override fun getRefreshToken(): String {
        return preferencesManager.getString(KEY_REFRESH_TOKEN)
    }

    override fun saveUserId(id: Int) {
        preferencesManager.saveInt(KEY_USER_ID, id)
    }

    override fun getUserId(): Int {
        return preferencesManager.getInt(KEY_USER_ID)
    }

    private fun retrieveExpiration(access: String) {
        val splitted = access.split('.')
        if (splitted.isNotEmpty()) {
            val decoded = splitted[1].fromBase64()
            Log.d("tokenBody", decoded)
            val tokenBody = Gson().fromJson(decoded, TokenBody::class.java)
            Log.d("tokenBody", tokenBody.exp.toString())
            preferencesManager.saveLong(KEY_REFRESH_TOKEN_EXPIRATION, tokenBody.exp)
        }
    }

    @Keep
    private data class TokenBody(val exp: Long)

}