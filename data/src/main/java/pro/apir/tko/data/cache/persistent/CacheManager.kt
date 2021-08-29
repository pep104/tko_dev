package pro.apir.tko.data.cache.persistent

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
interface CacheManager {

    fun lastUpdate(key: String): Long

}