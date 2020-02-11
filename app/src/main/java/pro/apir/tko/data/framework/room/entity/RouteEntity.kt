package pro.apir.tko.data.framework.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by antonsarmatin
 * Date: 2020-02-11
 * Project: tko-android
 */
//Нужен ли маршрут отдельно?
@Entity(tableName = "route_table")
data class RouteEntity(
        @PrimaryKey(autoGenerate = false)
        val id: Long?

)