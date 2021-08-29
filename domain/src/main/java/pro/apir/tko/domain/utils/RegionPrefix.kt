package pro.apir.tko.domain.utils

/**
 * Created by antonsarmatin
 * Date: 2020-05-02
 * Project: tko-android
 */
val REGION_PREFIXES = listOf("республика", "край", "область", "ао", "автономная", "автономный", "респ", "обл")
val LOCAL_AREA_PREFIXES = listOf("р-н")

fun substringLocationPrefixRecursively(location: String?): String? {
    val regionMatch = REGION_PREFIXES.filter { location?.contains(it, true) == true }
    val localMatch = LOCAL_AREA_PREFIXES.filter { location?.contains(it, true) == true }
    val delimiterMatch = location?.contains(',', true) == true

    if ((regionMatch.isNotEmpty() || localMatch.isNotEmpty()) && delimiterMatch){
        return substringLocationPrefixRecursively(location?.substringAfter(',')?.trim())
    }
    return location?.trim()
}

fun String.substringLocationPrefix() = substringLocationPrefixRecursively(this)