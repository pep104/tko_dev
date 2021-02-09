package pro.apir.tko.data.framework.manager.preferences

interface PreferencesManager {

    fun isExists(key: String): Boolean

    fun saveString(key: String, value: String)

    fun getString(key: String): String

    fun saveInt(key: String, value: Int)

    fun getInt(key: String): Int

    fun saveLong(key: String, value: Long)

    fun getLong(key: String): Long

    fun saveBool(key: String, value: Boolean)

    fun getBool(key: String): Boolean

    fun saveDouble(key: String, value: Double)

    fun getDouble(key: String): Double

}