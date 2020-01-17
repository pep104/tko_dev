package pro.apir.tko.data.repository.auth

import android.util.Log
import com.google.gson.Gson
import pro.apir.tko.core.constant.KEY_ACCESS_TOKEN
import pro.apir.tko.core.constant.KEY_REFRESH_TOKEN
import pro.apir.tko.core.constant.KEY_TOKEN_EXPIRATION
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.extension.fromBase64
import pro.apir.tko.core.functional.Either
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.preferences.PreferencesManager
import pro.apir.tko.data.repository.BaseRepository
import pro.apir.tko.domain.model.AuthTokenModel
import pro.apir.tko.domain.model.RefreshTokenModel
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi, private val preferencesManager: PreferencesManager) : AuthRepository, BaseRepository() {

    //TODO перенос реализации

    override suspend fun auth(email: String, password: String): Either<Failure, AuthTokenModel> {
        val result = request(authApi.auth(email, password)) { it.toModel() }
        if (result is Either.Right) {
            preferencesManager.saveString(KEY_ACCESS_TOKEN, result.b.access)
            preferencesManager.saveString(KEY_REFRESH_TOKEN, result.b.refresh)
            retrieveExpiration(result.b.access)
        }
        return result
    }

    override suspend fun refresh(refresh: String): Either<Failure, RefreshTokenModel> {
        val result = request(authApi.refresh(refresh)) { it.toModel() }
        if(result is Either.Right){
            preferencesManager.saveString(KEY_ACCESS_TOKEN, result.b.access)
            retrieveExpiration(result.b.access)
        }
        return result
    }

    fun retrieveExpiration(access: String) {
        val splitted = access.split('.')
        if(splitted.size>=1){
            val decoded = splitted[1].fromBase64()
            Log.d("tokenBody",decoded)
            val tokenBody = Gson().fromJson(decoded, TokenBody::class.java)
            Log.d("tokenBody", tokenBody.exp.toString())
            preferencesManager.saveLong(KEY_TOKEN_EXPIRATION, tokenBody.exp)
        }
    }

    data class TokenBody(val exp: Long)

}