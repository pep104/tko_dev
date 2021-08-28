package pro.apir.tko.data.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.apir.tko.core.constant.SUGGESTION_DETAILED_URL
import pro.apir.tko.core.constant.SUGGESTION_URL
import pro.apir.tko.data.BuildConfig
import pro.apir.tko.data.framework.manager.host.HostManager
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.NetworkHandler
import pro.apir.tko.data.framework.network.api.*
import pro.apir.tko.data.framework.network.authenticator.TokenAuthenticator
import pro.apir.tko.data.framework.network.calladapter.ApiCallAdapterFactory
import pro.apir.tko.data.framework.network.calladapter.ApiResponseTransformer
import pro.apir.tko.data.framework.network.interceptor.CacheInterceptor
import pro.apir.tko.data.framework.network.interceptor.DaDataTokenInterceptor
import pro.apir.tko.data.framework.network.interceptor.DynamicHostInterceptor
import pro.apir.tko.data.framework.network.interceptor.TokenInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by antonsarmatin
 * Date: 27/08/2021
 * Project: tko
 */

private const val AUTH_RETROFIT = "auth"
private const val SUGGESTION_RETROFIT = "suggestion"
private const val SUGGESTION_DETAILED_RETROFIT = "suggestionDetailed"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideCallAdapterFactory(transformer: ApiResponseTransformer) =
        ApiCallAdapterFactory.create(transformer)

    //Retrofit

    @Singleton
    @Provides
    fun provideRetrofitInterfaceMain(
        apiCallAdapterFactory: ApiCallAdapterFactory,
        dynamicHostInterceptor: DynamicHostInterceptor,
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(dynamicHostInterceptor)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL + BuildConfig.API_SUFFIX)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(apiCallAdapterFactory)
            .build()

    }

    @Named(AUTH_RETROFIT)
    @Singleton
    @Provides
    fun provideRetrofitAuth(
        apiCallAdapterFactory: ApiCallAdapterFactory,
        dynamicHostInterceptor: DynamicHostInterceptor,
    ): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(dynamicHostInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL + BuildConfig.API_SUFFIX)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(apiCallAdapterFactory)
            .build()
    }

    @Named(SUGGESTION_RETROFIT)
    @Singleton
    @Provides
    fun provideRetorifutAddress(
        apiCallAdapterFactory: ApiCallAdapterFactory,
        daDataTokenInterceptor: DaDataTokenInterceptor,
    ): Retrofit {
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
            .addCallAdapterFactory(apiCallAdapterFactory)
            .build()
    }

    @Named(SUGGESTION_DETAILED_RETROFIT)
    @Singleton
    @Provides
    fun provideRetorifutAddressDetailed(
        apiCallAdapterFactory: ApiCallAdapterFactory,
        daDataTokenInterceptor: DaDataTokenInterceptor,
    ): Retrofit {
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
            .addCallAdapterFactory(apiCallAdapterFactory)
            .build()
    }


    //Interceptors and etc.

    @Singleton
    @Provides
    fun dynamicHostInterceptor(hostManager: HostManager): DynamicHostInterceptor =
        DynamicHostInterceptor(hostManager)

    @Singleton
    @Provides
    fun authInterceptor(credentialsManager: CredentialsManager): TokenInterceptor =
        TokenInterceptor(credentialsManager)

    @Singleton
    @Provides
    fun authenticator(
        credentialsManager: CredentialsManager,
        authApi: AuthApi,
    ): TokenAuthenticator = TokenAuthenticator(credentialsManager, authApi)


    @Singleton
    @Provides
    fun daDataInterceptor(): DaDataTokenInterceptor = DaDataTokenInterceptor()

    @Singleton
    @Provides
    fun cacheInterceptor(networkHandler: NetworkHandler): CacheInterceptor =
        CacheInterceptor(networkHandler)

    //

    @Singleton
    @Provides
    fun authApi(@Named(AUTH_RETROFIT) retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Singleton
    @Provides
    fun inventoryApi(retrofit: Retrofit): InventoryApi = retrofit.create(InventoryApi::class.java)

    @Singleton
    @Provides
    fun routeApi(retrofit: Retrofit): RouteApi = retrofit.create(RouteApi::class.java)

    @Singleton
    @Provides
    fun routeTrackApi(retrofit: Retrofit): RouteTrackApi =
        retrofit.create(RouteTrackApi::class.java)

    @Singleton
    @Provides
    fun attachmentApi(retrofit: Retrofit): AttachmentApi =
        retrofit.create(AttachmentApi::class.java)

    @Singleton
    @Provides
    fun userApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Singleton
    @Provides
    fun suggestionApi(@Named(SUGGESTION_RETROFIT) retrofit: Retrofit): SuggestionApi =
        retrofit.create(
            SuggestionApi::class.java)

    @Singleton
    @Provides
    fun suggestionDetailedApi(@Named(SUGGESTION_DETAILED_RETROFIT) retrofit: Retrofit): SuggestionDetailedApi =
        retrofit.create(
            SuggestionDetailedApi::class.java)

}