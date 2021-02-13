package pro.apir.tko.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import pro.apir.tko.core.data.Resource
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository(private val credentialsManager: CredentialsManager?, private val tokenStrategy: TokenStrategy? = TokenStrategy.CHECK) {

    //Подумать над внедрением лишней зависимости,
    //мб есть вариант провернуть проверку истечения рефрештокена лучше,
    //чем внедряя менеджер в каждый класс наследующийся от базового репозитория

    protected suspend fun <T, R> request(call: suspend () -> Response<T>, transform: (T) -> R): Resource<R> {
        return if (tokenStrategy == TokenStrategy.NO_AUTH || (credentialsManager != null && !credentialsManager.isRefreshTokenExpired())) {
            try {
                val call = call.invoke()
                val body = call.body()
                when (call.isSuccessful && body != null) {
                    true -> Resource.Success(transform(body))
                    false -> {
                        Resource.Error(
                                Failure.ServerError(parseError(call.errorBody()?.string())
                                        ?: "Unknown Error")
                        )
                    }
                }
            } catch (exception: Throwable) {
                Log.e("failure", "Failure: " + exception.localizedMessage)
                when (exception) {
                    is IOException -> {
                        Resource.Error(Failure.Ignore)
                    }
                    //WARNING
                    is CancellationException -> {
                        Resource.Error(Failure.Ignore)
                    }
                    else -> {
                        Resource.Error(Failure.ServerError())
                    }
                }
            }
        } else {
            Resource.Error(Failure.RefreshTokenExpired)
        }
    }

    enum class TokenStrategy {
        CHECK,
        NO_AUTH
    }


    private fun parseError(string: String?): String? {
        if (string == null)
            return null

        //TODO Parse error with ObjectRequestErrorModels - not ready yet
        return try {
            Log.e("http", "Server error: $string")
            val errorResponse: JsonObject = Gson().fromJson(string, JsonObject::class.java)
            val message = errorResponse.get("code").asString
            message
        } catch (e: Exception) {
            null
        }
    }

}