package pro.apir.tko.data.framework.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by antonsarmatin
 * Date: 2020-02-11
 * Project: tko-android
 */
//Route or points
//Completed points by session?
//???
@Entity(tableName = "route_session_table")
data class RouteSessionEntity(

        @PrimaryKey(autoGenerate = true)
        val id: Long?,

        @ColumnInfo(name = "user_id")
        val userId: Int,

        @ColumnInfo(name = "route_id")
        val routeId: Int,

        val date: String,

        @ColumnInfo(name = "is_completed")
        val isCompleted: Boolean

)