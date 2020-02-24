package pro.apir.tko.data.framework.room.dao

import androidx.room.*
import pro.apir.tko.data.framework.room.entity.PointEntity
import pro.apir.tko.data.framework.room.entity.RouteSessionEntity
import pro.apir.tko.data.framework.room.entity.relation.RouteSessionWithPoints

/**
 * Created by antonsarmatin
 * Date: 2020-02-11
 * Project: tko-android
 */
@Dao
interface RouteSessionDao {


    @Transaction
    @Query("SELECT * FROM route_session_table where user_id LIKE :userId and route_id LIKE :routeId and date LIKE :date LIMIT 1")
    suspend fun getSession(userId: Int, routeId: Int, date: String): List<RouteSessionWithPoints>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSession(session: RouteSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoint(point: PointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoints(points: List<PointEntity>): List<Long>

}