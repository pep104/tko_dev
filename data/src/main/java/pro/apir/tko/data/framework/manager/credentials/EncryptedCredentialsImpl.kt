package pro.apir.tko.data.framework.manager.credentials

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedCredentialsImpl(context: Context) : EncryptedCredentials {

    val alias = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    } else {
        MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .build()
    }

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "apirtkoapp",
        alias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val KEY_1 = "first_secured_key"
    private val KEY_2 = "second_secured_key"

    override fun saveCredentials(login: String, password: String) {
        sharedPreferences
            .edit()
            .apply {
                putString(KEY_1, login)
                putString(KEY_2, password)
            }
            .apply()
    }

    override fun getCredentials(): Pair<String, String> {
        val k1 = sharedPreferences.getString(KEY_1, "") ?: ""
        val k2 = sharedPreferences.getString(KEY_2, "") ?: ""
        return k1 to k2
    }

    override fun clear() {
        saveCredentials("", "")
    }
}