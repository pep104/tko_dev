package pro.apir.tko.data.framework.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pro.apir.tko.BuildConfig
import pro.apir.tko.data.framework.room.dao.PointDao
import pro.apir.tko.data.framework.room.dao.RouteDao
import pro.apir.tko.data.framework.room.dao.RouteSessionDao
import pro.apir.tko.data.framework.room.entity.PointEntity
import pro.apir.tko.data.framework.room.entity.RouteSessionEntity

/**
 * Created by antonsarmatin
 * Date: 2020-02-11
 * Project: tko-android
 */
@Database(entities = [RouteSessionEntity::class, PointEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeSessionDao(): RouteSessionDao

    abstract fun routeDao(): RouteDao

    abstract fun pointDao(): PointDao

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
                    if (BuildConfig.DEBUG)
                        fallbackToDestructiveMigration()
                }.build()

                INSTANCE = instance
                return instance
            }
        }


    }


}