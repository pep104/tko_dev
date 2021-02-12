package pro.apir.tko.data.di

import android.app.Application
import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.apir.tko.core.constant.BASE_URL
import pro.apir.tko.core.constant.SUGGESTION_DETAILED_URL
import pro.apir.tko.core.constant.SUGGESTION_URL
import pro.apir.tko.data.framework.dict.OptionsDictionariesManager
import pro.apir.tko.data.framework.dict.OptionsDictionariesManagerImpl
import pro.apir.tko.data.framework.manager.location.LocationManager
import pro.apir.tko.data.framework.manager.location.LocationManagerImpl
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.preferences.PreferencesManagerImpl
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.manager.token.CredentialsManagerImpl
import pro.apir.tko.data.framework.network.NetworkHandler
import pro.apir.tko.data.framework.network.api.*
import pro.apir.tko.data.framework.network.authenticator.TokenAuthenticator
import pro.apir.tko.data.framework.network.interceptor.AuthTokenRequestInterceptor
import pro.apir.tko.data.framework.network.interceptor.CacheInterceptor
import pro.apir.tko.data.framework.network.interceptor.DaDataTokenInterceptor
import pro.apir.tko.data.framework.room.AppDatabase
import pro.apir.tko.data.mapper.TrackingFailureCodeMapper
import pro.apir.tko.data.mapper.TrackingFailureCodeMapperImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class FrameworkModule(private val application: Application) {


    //    @Named("main")
    @Singleton
    @Provides
    fun provideRetrofitInterfaceMain(authTokenRequestInterceptor: AuthTokenRequestInterceptor, tokenAuthenticator: TokenAuthenticator): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(authTokenRequestInterceptor)
                .addInterceptor(loggingInterceptor)
                .authenticator(tokenAuthenticator)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

    }

    @Named("auth")
    @Singleton
    @Provides
    fun provideRetrofitAuth(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Named("suggestion")
    @Singleton
    @Provides
    fun provideRetorifutAddress(daDataTokenInterceptor: DaDataTokenInterceptor): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(daDataTokenInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
                .baseUrl(SUGGESTION_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Named("suggestionDetailed")
    @Singleton
    @Provides
    fun provideRetorifutAddressDetailed(daDataTokenInterceptor: DaDataTokenInterceptor): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(daDataTokenInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
                .baseUrl(SUGGESTION_DETAILED_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }



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

    //Interceptors and etc.

    @Singleton
    @Provides
    fun authInterceptor(sp: PreferencesManager): AuthTokenRequestInterceptor = AuthTokenRequestInterceptor(sp)

    @Singleton
    @Provides
    fun daDataInterceptor(): DaDataTokenInterceptor = DaDataTokenInterceptor()

    @Singleton
    @Provides
    fun cacheInterceptor(networkHandler: NetworkHandler): CacheInterceptor = CacheInterceptor(networkHandler)

    @Singleton
    @Provides
    fun authenticator(credentialsManager: CredentialsManager, authApi: AuthApi): TokenAuthenticator = TokenAuthenticator(credentialsManager, authApi)

    //

    @Singleton
    @Provides
    fun networkHandler(context: Context): NetworkHandler = NetworkHandler(context)

    //

    @Singleton
    @Provides
    fun inventoryApi(retrofit: Retrofit): InventoryApi = retrofit.create(InventoryApi::class.java)

    @Singleton
    @Provides
    fun routeApi(retrofit: Retrofit): RouteApi = retrofit.create(RouteApi::class.java)

    @Singleton
    @Provides
    fun routeTrackApi(retrofit: Retrofit): RouteTrackApi = retrofit.create(RouteTrackApi::class.java)

    @Singleton
    @Provides
    fun authApi(@Named("auth") retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Singleton
    @Provides
    fun attachmentApi(retrofit: Retrofit): AttachmentApi = retrofit.create(AttachmentApi::class.java)

    @Singleton
    @Provides
    fun suggestionApi(@Named("suggestion") retrofit: Retrofit): SuggestionApi = retrofit.create(SuggestionApi::class.java)

    @Singleton
    @Provides
    fun suggestionDetailedApi(@Named("suggestionDetailed") retrofit: Retrofit): SuggestionDetailedApi = retrofit.create(SuggestionDetailedApi::class.java)

    @Singleton
    @Provides
    fun userApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

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