package pro.apir.tko.domain.model.host

import androidx.annotation.Keep

/**
 * Created by antonsarmatin
 * Date: 27/08/2021
 * Project: tko
 */
@Keep
enum class Host(val scheme: String, val host: String) {

    APIR("https","test.tko.apir.pro"),
    TBO_ECOSERVICE("http","37.139.32.239"),
    PRIMORIE("http","176.118.22.114");

    fun toUrl() = "${scheme}://${host}"

}