package pro.apir.tko.domain.interactors.host

import pro.apir.tko.domain.model.host.Host
import pro.apir.tko.domain.repository.host.HostRepository
import javax.inject.Inject

class HostInteractorImpl @Inject constructor(
    private val hostRepository: HostRepository,
) : HostInteractor {

    override fun save(host: Host) = hostRepository.save(host)

    override fun getHost() = hostRepository.getHost()

}