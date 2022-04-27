package pro.apir.tko.data.framework.room.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.data.framework.room.entity.PointEntity

/**
 * Created by Антон Сарматин
 * Date: 19.03.2020
 * Project: tko-android
 */
data class PointWithPhotos(

        @Embedded
        val point: PointEntity,

        @Relation(parentColumn = "id", entityColumn = "point_id")
        val photos: List<PhotoEntity>

)