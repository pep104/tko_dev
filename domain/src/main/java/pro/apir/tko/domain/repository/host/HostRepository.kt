package pro.apir.tko.domain.repository.host

import pro.apir.tko.domain.model.host.Host

/**
 * Created by antonsarmatin
 * Date: 27/08/2021
 * Project: tko
 */
interface HostRepository {

    fun save(host: Host)

    fun getHost(): Host

}