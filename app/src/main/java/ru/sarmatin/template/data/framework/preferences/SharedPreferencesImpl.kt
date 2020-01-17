package ru.sarmatin.template.data.framework.preferences

import android.content.Context
import android.preference.PreferenceManager
import javax.inject.Inject

class SharedPreferencesImpl @Inject constructor(context: Context) : SharedPreferences {

    private val sp : android.content.SharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun saveString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String = sp.getString(key, "")

    override fun saveInt(key: String, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    override fun getInt(key: String): Int = sp.getInt(key, -1)

    override fun saveBool(key: String, value: Boolean) {
        sp.edit().putBoolean(key,value).apply()
    }

    override fun getBool(key: String) = sp.getBoolean(key, false)
}