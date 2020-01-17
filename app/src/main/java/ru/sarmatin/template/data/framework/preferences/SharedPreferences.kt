package ru.sarmatin.template.data.framework.preferences

interface SharedPreferences {

    fun saveString(key: String, value: String)

    fun getString(key: String): String

    fun saveInt(key: String, value: Int)

    fun getInt(key: String): Int

    fun saveBool(key: String, value: Boolean)

    fun getBool(key: String): Boolean

}