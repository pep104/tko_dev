package pro.apir.tko.domain.repository.blocked

import kotlinx.coroutines.flow.Flow

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
interface BlockedRepository {

    fun getBlock(): Flow<Boolean>

    fun fetch()

}