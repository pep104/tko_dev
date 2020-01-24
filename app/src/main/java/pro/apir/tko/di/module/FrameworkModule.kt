package pro.apir.tko.di.module

import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.apir.tko.core.constant.BASE_URL
import pro.apir.tko.core.constant.SUGGESTION_DETAILED_URL
import pro.apir.tko.core.constant.SUGGESTION_URL
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.preferences.PreferencesManagerImpl
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.manager.token.TokenManagerImpl
import pro.apir.tko.data.framework.network.NetworkHandler
import pro.apir.tko.data.framework.network.api.SuggestionApi
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.framework.network.api.SuggestionDetailedApi
import pro.apir.tko.data.framework.network.authenticator.TokenAuthenticator
import pro.apir.tko.data.framework.network.interceptor.AuthTokenRequestInterceptor
import pro.apir.tko.data.framework.network.interceptor.CacheInterceptor
import pro.apir.tko.data.framework.network.interceptor.DaDataTokenInterceptor
import pro.apir.tko.data.framework.source.address.SuggestionDetailedSource
import pro.apir.tko.data.framework.source.address.SuggestionSource
import pro.apir.tko.data.framework.source.auth.AuthSource
import pro.apir.tko.data.framework.source.inventory.InventorySource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class FrameworkModule {


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


//    @Singleton
//    @Provides
//    fun provideMainGetApi(retrofit: Retrofit) = retrofit.create(InventoryApi::class.java)
//
//    @Singleton
//    @Provides
//    fun provideAuthApi(@Named("auth") retrofit: Retrofit) = retrofit.create(AuthApi::class.java)

    //Managers

    @Singleton
    @Provides
    fun preferencesManager(context: Context): PreferencesManager = PreferencesManagerImpl(context)

    @Singleton
    @Provides
    fun tokenManager(preferencesManager: PreferencesManager): TokenManager = TokenManagerImpl(preferencesManager)

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
    fun authenticator(tokenManager: TokenManager, authApi: AuthApi): TokenAuthenticator = TokenAuthenticator(tokenManager, authApi)

    //

    @Singleton
    @Provides
    fun networkHandler(context: Context): NetworkHandler = NetworkHandler(context)

    //

    @Singleton
    @Provides
    fun inventoryApi(retrofit: Retrofit): InventoryApi = InventorySource(retrofit)

    @Singleton
    @Provides
    fun authApi(@Named("auth") retrofit: Retrofit): AuthApi = AuthSource(retrofit)

    @Singleton
    @Provides
    fun suggestionApi(@Named("suggestion") retrofit: Retrofit): SuggestionApi = SuggestionSource(retrofit)

    @Singleton
    @Provides
    fun suggestionDetailedApo(@Named("suggestionDetailed") retrofit: Retrofit): SuggestionDetailedApi = SuggestionDetailedSource(retrofit)

}