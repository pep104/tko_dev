package pro.apir.tko.data.framework.manager.preferences

interface PreferencesManager {

    fun isExists(key: String): Boolean

    fun saveString(key: String, value: String)

    fun getString(key: String, default: String = ""): String

    fun saveInt(key: String, value: Int)

    fun getInt(key: String, default: Int = 0): Int

    fun saveLong(key: String, value: Long)

    fun getLong(key: String, default: Long = 0): Long

    fun saveBool(key: String, value: Boolean)

    fun getBool(key: String, default: Boolean = false): Boolean

    fun saveDouble(key: String, value: Double)

    fun getDouble(key: String, default: Double = 0.0): Double

}