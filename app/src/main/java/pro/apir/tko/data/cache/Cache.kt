package pro.apir.tko.data.cache

/**
 * Created by antonsarmatin
 * Date: 19/05/2020
 * Project: tko-android
 */
open class Cache<T> {

    private val data = mutableMapOf<String, T>()

    fun put(key: String, value: T): T {
        synchronized(data) {
            data[key] = value
            return value
        }
    }

    fun find(key: String): T? {
        synchronized(data) {
            return data[key]
        }
    }

    fun getAll(): List<T>? {
        synchronized(data) {
            return data.toList().map { it.second }
        }
    }

    fun clear() {
        synchronized(data) {
            data.clear()
        }
    }


}