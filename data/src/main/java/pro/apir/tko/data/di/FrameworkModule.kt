package pro.apir.tko.data.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import pro.apir.tko.data.framework.dict.OptionsDictionariesManager
import pro.apir.tko.data.framework.dict.OptionsDictionariesManagerImpl
import pro.apir.tko.data.framework.manager.host.HostManager
import pro.apir.tko.data.framework.manager.host.HostManagerImpl
import pro.apir.tko.data.framework.manager.location.LocationManagerImpl
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.preferences.PreferencesManagerImpl
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.manager.token.CredentialsManagerImpl
import pro.apir.tko.data.framework.network.NetworkHandler
import pro.apir.tko.data.framework.room.AppDatabase
import pro.apir.tko.data.mapper.TrackingFailureCodeMapper
import pro.apir.tko.data.mapper.TrackingFailureCodeMapperImpl
import pro.apir.tko.domain.manager.LocationManager
import javax.inject.Singleton


@Module
class FrameworkModule(private val application: Application) {


    //Managers

    @Singleton
    @Provides
    fun preferencesManager(context: Context): PreferencesManager = PreferencesManagerImpl(context)

    @Singleton
    @Provides
    fun tokenManager(preferencesManager: PreferencesManager): CredentialsManager = CredentialsManagerImpl(preferencesManager)

    @Singleton
    @Provides
    fun locationManager(context: Context, preferencesManager: PreferencesManager): LocationManager = LocationManagerImpl(context, preferencesManager)

    @Singleton
    @Provides
    fun hostManager(preferencesManager: PreferencesManager): HostManager = HostManagerImpl(preferencesManager)

     //

    @Singleton
    @Provides
    fun networkHandler(context: Context): NetworkHandler = NetworkHandler(context)


    //Room

    @Singleton
    @Provides
    fun appDatabase(): AppDatabase {
        return AppDatabase.getDatabase(application)
    }

    @Singleton
    @Provides
    fun routeSessionDao(appDatabase: AppDatabase) = appDatabase.routeSessionDao()

    @Singleton
    @Provides
    fun routePointDao(appDatabase: AppDatabase) = appDatabase.pointDao()

    @Singleton
    @Provides
    fun routePhotosDao(appDatabase: AppDatabase) = appDatabase.photoDao()


    //

    @Provides
    @Singleton
    fun provideDictManager(context: Context): OptionsDictionariesManager = OptionsDictionariesManagerImpl(context)

    //Mapper

    @Provides
    @Singleton
    fun trackingFailureMapper(): TrackingFailureCodeMapper = TrackingFailureCodeMapperImpl()

}