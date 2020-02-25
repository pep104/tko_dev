package pro.apir.tko.data.framework.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by antonsarmatin
 * Date: 2020-02-11
 * Project: tko-android
 */

/**
 *
 *  Entity for saving RouteSession's Point state to local database
 *
 */
@Entity(tableName = "point_table")
data class PointEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,

        @ColumnInfo(name = "container_id")
        val containerId: Int,

        val type: Int,

        //Relation
        @ColumnInfo(name = "session_id")
        val sessionId: Long

)