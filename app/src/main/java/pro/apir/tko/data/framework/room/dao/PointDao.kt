package pro.apir.tko.data.framework.room.dao

import androidx.room.Dao
import androidx.room.Update
import pro.apir.tko.data.framework.room.entity.PointEntity

/**
 * Created by antonsarmatin
 * Date: 2020-02-12
 * Project: tko-android
 */
@Dao
interface PointDao {

    //Update
    @Update
    fun updatePoint(point: PointEntity)

}