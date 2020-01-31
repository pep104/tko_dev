package pro.apir.tko.core.types

/**
 * Created by antonsarmatin
 * Date: 2020-01-31
 * Project: tko-android
 */
class Dictionary : LinkedHashMap<String, String>()

public fun dictOf(vararg pairs: Pair<String, String>) = Dictionary().apply { putAll(pairs.toMap()) }