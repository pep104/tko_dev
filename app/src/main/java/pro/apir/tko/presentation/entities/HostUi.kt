package pro.apir.tko.presentation.entities

import androidx.annotation.Keep
import androidx.annotation.StringRes
import pro.apir.tko.R
import pro.apir.tko.domain.model.host.Host

/**
 * Created by antonsarmatin
 * Date: 27/08/2021
 * Project: tko
 */
@Keep
enum class HostUi(@StringRes val stringId: Int) {

    APIR(R.string.host_apir),
    TBO_ECOSERVICE(R.string.host_tbo_ecoservice),
    PRIMORIE(R.string.host_primorie);

    fun toDomain(): Host {
        return when (this) {
            APIR -> Host.APIR
            TBO_ECOSERVICE -> Host.TBO_ECOSERVICE
            PRIMORIE -> Host.PRIMORIE
        }
    }

    companion object {

        fun fromDomain(host: Host): HostUi {
            return when (host) {
                Host.APIR -> APIR
                Host.TBO_ECOSERVICE -> TBO_ECOSERVICE
                Host.PRIMORIE -> PRIMORIE
            }
        }

    }

}