package pro.apir.tko.core.types

/**
 * Created by antonsarmatin
 * Date: 2020-01-31
 * Project: tko-android
 */
class Dictionary : LinkedHashMap<String, String>() {

    fun getPositionByKey(key: String) = keys.indexOf(key)

    fun getPositionByValue(value: String) = values.indexOf(value)

    fun getKey(pos: Int?): String? {
        if(pos != null){
            val kk = keys.toList()
            return kk[pos]
        }else{
            return null
        }
    }

}

public fun dictOf(vararg pairs: Pair<String, String>) = Dictionary().apply { putAll(pairs.toMap()) }