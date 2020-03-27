package pro.apir.tko.data.framework.room.dao

import androidx.room.*
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
    @Query("SELECT * FROM route_session_table where user_id LIKE :userId and dateLong BETWEEN :startDateTime AND :endDateTime")
    suspend fun getExistingSession(userId: Int, startDateTime: Long, endDateTime: Long): List<RouteSessionWithPoints>

    @Transaction
    @Query("SELECT * FROM route_session_table where user_id LIKE :userId and route_id LIKE :routeId and dateLong BETWEEN :startDateTime AND :endDateTime")
    suspend fun getSession(userId: Int, routeId: Int, startDateTime: Long, endDateTime: Long): List<RouteSessionWithPoints>

    @Transaction
    @Query("SELECT * FROM route_session_table where id = :id")
    suspend fun getSession(id: Long): RouteSessionEntity?

    @Transaction
    @Query("SELECT * FROM route_session_table where id = :id")
    suspend fun getSessionWithPoints(id: Long): RouteSessionWithPoints

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSession(session: RouteSessionEntity): Long

    @Update
    fun updateSession(session: RouteSessionEntity)

}