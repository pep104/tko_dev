package pro.apir.tko.data.framework.manager.preferences

import android.content.Context
import android.preference.PreferenceManager
import javax.inject.Inject

class PreferencesManagerImpl @Inject constructor(context: Context) : PreferencesManager {

    private val sp: android.content.SharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun isExists(key: String): Boolean {
        return sp.contains(key)
    }

    override fun saveString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    override fun getString(key: String, default: String): String = sp.getString(key, default) ?: default

    override fun saveInt(key: String, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    override fun getInt(key: String, default: Int): Int = sp.getInt(key, default)

    override fun saveLong(key: String, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String, default: Long) = sp.getLong(key, default)

    override fun saveBool(key: String, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    override fun getBool(key: String, default: Boolean) = sp.getBoolean(key, default)

    override fun saveDouble(key: String, value: Double) {
        sp.edit().putFloat(key, value.toFloat()).apply()
    }

    override fun getDouble(key: String, default: Double): Double = sp.getFloat(key, default.toFloat()).toDouble()
}