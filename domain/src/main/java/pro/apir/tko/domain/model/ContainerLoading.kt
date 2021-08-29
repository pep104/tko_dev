package pro.apir.tko.domain.model

import androidx.annotation.Keep

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */
@Keep
enum class ContainerLoading {
    SIDE,
    REAR,
    FRONT,
    PORTAL,
    CRANE,
    MULTILIFT
}