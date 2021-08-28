package pro.apir.tko.domain.interactors.blocked

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.domain.repository.blocked.BlockedRepository
import javax.inject.Inject

class BlockedInteractorImpl @Inject constructor(
    private val blockedRepository: BlockedRepository,
) : BlockedInteractor {

    override fun getBlock(): Flow<Boolean> = blockedRepository.getBlock()

    override fun fetch() = blockedRepository.fetch()
}