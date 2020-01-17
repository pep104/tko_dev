package ru.sarmatin.template.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.sarmatin.template.core.constant.BASE_URL
import ru.sarmatin.template.data.framework.network.NetworkHandler
import ru.sarmatin.template.data.framework.network.api.ExampleApi
import ru.sarmatin.template.data.framework.network.interceptor.AuthTokenRequestInterceptor
import ru.sarmatin.template.data.framework.network.interceptor.CacheInterceptor
import ru.sarmatin.template.data.framework.preferences.SharedPreferences
import ru.sarmatin.template.data.framework.preferences.SharedPreferencesImpl
import ru.sarmatin.template.data.framework.source.example.ExampleSource
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class FrameworkModule {


    //    @Named("main")
    @Singleton
    @Provides
    fun provideRetrofitInterfaceMain(): Retrofit {
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
    fun sharedPreferences(context: Context): SharedPreferences = SharedPreferencesImpl(context)

    @Singleton
    @Provides
    fun authInterceptor(sp: SharedPreferences): AuthTokenRequestInterceptor = AuthTokenRequestInterceptor(sp)

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

}