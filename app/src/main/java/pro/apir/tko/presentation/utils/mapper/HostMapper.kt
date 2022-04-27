package pro.apir.tko.presentation.utils.mapper

import android.content.Context
import pro.apir.tko.domain.model.host.Host
import pro.apir.tko.presentation.entities.HostUi
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
interface HostMapper {

    fun map(name: String): HostUi

    fun mapToDomain(name: String): Host

}

class ContextHostMapper @Inject constructor(private val context: Context) : HostMapper {

    override fun map(name: String): HostUi {
        return HostUi.values().firstOrNull { context.getString(it.stringId) == name } ?: HostUi.APIR
    }

    override fun mapToDomain(name: String) = map(name).toDomain()

}