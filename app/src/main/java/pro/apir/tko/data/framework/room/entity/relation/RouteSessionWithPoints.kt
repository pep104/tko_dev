package pro.apir.tko.data.framework.room.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import pro.apir.tko.data.framework.room.entity.PointEntity
import pro.apir.tko.data.framework.room.entity.RouteSessionEntity

/**
 * Created by Антон Сарматин
 * Date: 15.02.2020
 * Project: tko-android
 */
data class RouteSessionWithPoints(

        @Embedded
        val session: RouteSessionEntity,

        @Relation(parentColumn = "id", entityColumn = "session_id")
        val points: List<PointEntity>

)