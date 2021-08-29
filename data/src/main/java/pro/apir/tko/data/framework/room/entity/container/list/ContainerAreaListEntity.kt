package pro.apir.tko.data.framework.room.entity.container.list

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import pro.apir.tko.domain.model.CoordinatesModel

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */

const val CONTAINER_AREA_LIST_TABLE_NAME = "container_area_list_table"

@Entity(tableName = CONTAINER_AREA_LIST_TABLE_NAME)
data class ContainerAreaListEntity(
    @PrimaryKey
    val id: Long,
    val identifier: String,
    val registryNumber: String,
    val location: String,
    val status: String,
    @Embedded
    val coordinates: CoordinatesModel?,
    val containersCount: Int,
    val area: Double,
    val resourceType: String,
)