package pro.apir.tko.data.framework.manager.host

import pro.apir.tko.domain.model.host.Host

/**
* Created by antonsarmatin
* Date: 27/08/2021
* Project: tko
*/
interface HostManager {

    fun saveHost(host: Host)

    fun getHost(): Host

}