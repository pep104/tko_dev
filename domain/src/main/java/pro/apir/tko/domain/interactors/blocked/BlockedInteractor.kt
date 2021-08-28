package pro.apir.tko.domain.interactors.blocked

import kotlinx.coroutines.flow.Flow

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
interface BlockedInteractor {

    fun getBlock(): Flow<Boolean>

    fun fetch()

}