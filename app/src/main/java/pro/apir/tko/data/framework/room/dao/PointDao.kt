package pro.apir.tko.data.framework.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import pro.apir.tko.data.framework.room.entity.PointEntity

/**
 * Created by antonsarmatin
 * Date: 2020-02-12
 * Project: tko-android
 */
@Dao
interface PointDao {

    @Transaction
    @Query("SELECT * FROM point_table where id = :pointId ")
    fun getPoint(pointId: Long): PointEntity

    //Update
    @Update
    fun updatePoint(point: PointEntity)

}