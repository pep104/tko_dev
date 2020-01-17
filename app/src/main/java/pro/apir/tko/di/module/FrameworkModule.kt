package pro.apir.tko.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import pro.apir.tko.core.constant.BASE_URL
import pro.apir.tko.data.framework.network.NetworkHandler
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.network.api.ExampleApi
import pro.apir.tko.data.framework.network.interceptor.AuthTokenRequestInterceptor
import pro.apir.tko.data.framework.network.interceptor.CacheInterceptor
import pro.apir.tko.data.framework.preferences.PreferencesManager
import pro.apir.tko.data.framework.preferences.PreferencesManagerImpl
import pro.apir.tko.data.framework.source.auth.AuthSource
import pro.apir.tko.data.framework.source.example.ExampleSource
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class FrameworkModule {


    //    @Named("main")
    @Singleton
    @Provides
    fun provideRetrofitInterfaceMain(authTokenRequestInterceptor: AuthTokenRequestInterceptor): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(authTokenRequestInterceptor)
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


//    @Singleton
//    @Provides
//    fun provideMainGetApi(@Named("main") retrofit: Retrofit): ExampleApi {
//        return retrofit.create(ExampleApi::class.java)
//    }


    @Singleton
    @Provides
    fun preferencesManager(context: Context): PreferencesManager = PreferencesManagerImpl(context)

    @Singleton
    @Provides
    fun authInterceptor(sp: PreferencesManager): AuthTokenRequestInterceptor = AuthTokenRequestInterceptor(sp)

    @Singleton
    @Provides
    fun cacheInterceptor(networkHandler: NetworkHandler): CacheInterceptor = CacheInterceptor(networkHandler)

    //

    @Singleton
    @Provides
    fun networkHandler(context: Context): NetworkHandler = NetworkHandler(context)

    //

    @Singleton
    @Provides
    fun exampleApi(retrofit: Retrofit): ExampleApi = ExampleSource(retrofit)

    @Singleton
    @Provides
    fun authApi(@Named("auth") retrofit: Retrofit): AuthApi = AuthSource(retrofit)

}