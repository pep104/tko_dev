package pro.apir.tko.data.framework.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
@Entity(tableName = "photo_table")
data class PhotoEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,

        @ColumnInfo(name = "point_id")
        val point: Long,

        val type: String,

        val path: String

)