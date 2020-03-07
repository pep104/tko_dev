package pro.apir.tko.data.framework.room.dao

import androidx.room.*
import pro.apir.tko.data.framework.room.entity.PhotoEntity

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
@Dao
interface PhotoDao {

    @Transaction
    @Query("SELECT * FROM photo_table WHERE id = :id")
    fun get(id: Long): PhotoEntity

    @Update
    fun update(point: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(point: PhotoEntity): Long

    @Delete
    fun delete(point: PhotoEntity)

    @Transaction
    @Query("SELECT * FROM photo_table where point_id LIKE :pointId")
    fun selectAllPhotosByPoint(pointId: Long): List<PhotoEntity>

}