package pro.apir.tko.data.repository.host

import pro.apir.tko.data.framework.manager.host.HostManager
import pro.apir.tko.domain.model.host.Host
import pro.apir.tko.domain.repository.host.HostRepository
import javax.inject.Inject

class HostRepositoryImpl @Inject constructor(
    private val hostManager: HostManager,
) : HostRepository {

    override fun save(host: Host) = hostManager.saveHost(host)

    override fun getHost() = hostManager.getHost()

}