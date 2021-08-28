package pro.apir.tko.data.framework.room.dao.container

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pro.apir.tko.data.framework.room.entity.container.list.ContainerAreaListEntity

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
@Dao
interface ContainerAreaListCacheDao {

    @Query("SELECT * FROM CONTAINER_AREA_LIST_TABLE")
    fun getAll(): Flow<ContainerAreaListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(containerAreaListEntity: ContainerAreaListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg containerAreaListEntities: ContainerAreaListEntity)

    @Query("DELETE FROM CONTAINER_AREA_LIST_TABLE WHERE id = :id")
    fun delete(id: Long)

    @Transaction
    fun deleteByIds(ids: List<Long>) {
        ids.forEach {
            delete(it)
        }
    }
}