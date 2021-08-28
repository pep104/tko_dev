package pro.apir.tko.data.framework.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.room.dao.PointDao
import pro.apir.tko.data.framework.room.dao.RouteSessionDao
import pro.apir.tko.data.framework.room.dao.container.ContainerAreaListCacheDao
import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.data.framework.room.entity.PointEntity
import pro.apir.tko.data.framework.room.entity.RouteSessionEntity
import pro.apir.tko.data.framework.room.entity.container.list.ContainerAreaListEntity

/**
 * Created by antonsarmatin
 * Date: 2020-02-11
 * Project: tko-android
 */
@Database(
    entities = [
        RouteSessionEntity::class,
        PointEntity::class,
        PhotoEntity::class,
        ContainerAreaListEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeSessionDao(): RouteSessionDao

    abstract fun pointDao(): PointDao

    abstract fun photoDao(): PhotoDao

    abstract fun containerAreaListDao(): ContainerAreaListCacheDao

    companion object {


        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                ).apply {
//                    if (BuildConfig.DEBUG)
                        fallbackToDestructiveMigration()
                }.build()

                INSTANCE = instance
                return instance
            }
        }


    }


}