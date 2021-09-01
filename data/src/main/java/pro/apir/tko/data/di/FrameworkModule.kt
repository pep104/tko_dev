package pro.apir.tko.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pro.apir.tko.data.framework.dict.OptionsDictionariesManager
import pro.apir.tko.data.framework.dict.OptionsDictionariesManagerImpl
import pro.apir.tko.data.framework.manager.credentials.CredentialsManager
import pro.apir.tko.data.framework.manager.credentials.CredentialsManagerImpl
import pro.apir.tko.data.framework.manager.credentials.EncryptedCredentials
import pro.apir.tko.data.framework.manager.credentials.EncryptedCredentialsImpl
import pro.apir.tko.data.framework.manager.host.HostManager
import pro.apir.tko.data.framework.manager.host.HostManagerImpl
import pro.apir.tko.data.framework.manager.location.LocationManagerImpl
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.preferences.PreferencesManagerImpl
import pro.apir.tko.data.framework.network.NetworkHandler
import pro.apir.tko.data.framework.room.AppDatabase
import pro.apir.tko.data.mapper.TrackingFailureCodeMapper
import pro.apir.tko.data.mapper.TrackingFailureCodeMapperImpl
import pro.apir.tko.data.util.compressor.CompressorImpl
import pro.apir.tko.data.util.compressor.ImageCompressor
import pro.apir.tko.domain.manager.LocationManager
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class FrameworkModule() {


    //Managers

    @Singleton
    @Provides
    fun preferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManagerImpl(context)

    @Singleton
    @Provides
    fun credentialsManager(
        preferencesManager: PreferencesManager,
        encryptedCredentials: EncryptedCredentials,
    ): CredentialsManager = CredentialsManagerImpl(preferencesManager, encryptedCredentials)

    @Singleton
    @Provides
    fun locationManager(
        @ApplicationContext context: Context,
        preferencesManager: PreferencesManager,
    ): LocationManager = LocationManagerImpl(context, preferencesManager)

    @Singleton
    @Provides
    fun hostManager(preferencesManager: PreferencesManager): HostManager =
        HostManagerImpl(preferencesManager)

    @Singleton
    @Provides
    fun encryptedCredentials(@ApplicationContext context: Context): EncryptedCredentials =
        EncryptedCredentialsImpl(context)

    //

    @Singleton
    @Provides
    fun networkHandler(@ApplicationContext context: Context): NetworkHandler =
        NetworkHandler(context)


    //Room

    @Singleton
    @Provides
    fun appDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
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
    fun provideDictManager(@ApplicationContext context: Context): OptionsDictionariesManager =
        OptionsDictionariesManagerImpl(context)

    //Mapper

    @Provides
    @Singleton
    fun trackingFailureMapper(): TrackingFailureCodeMapper = TrackingFailureCodeMapperImpl()

    //LocationUtils
    @Provides
    @Singleton
    fun imageCompressor(@ApplicationContext context: Context): ImageCompressor =
        CompressorImpl(context)

}