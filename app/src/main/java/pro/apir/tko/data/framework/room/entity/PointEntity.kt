package pro.apir.tko.data.framework.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by antonsarmatin
 * Date: 2020-02-11
 * Project: tko-android
 */
@Entity(tableName = "point_table")
data class PointEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long?

        //id контейнерной площадки

        //статус

)