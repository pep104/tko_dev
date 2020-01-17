package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.source.auth.AuthSource
import pro.apir.tko.data.framework.source.example.ExampleSource
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.auth.AuthRepositoryImpl
import pro.apir.tko.data.repository.example.ExampleRepository
import pro.apir.tko.data.repository.example.ExampleRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun exampleRepository(exampleSource: ExampleSource): ExampleRepository = ExampleRepositoryImpl(exampleSource)

    @Provides
    @Singleton
    fun authRepository(authApi: AuthApi): AuthRepository = AuthRepositoryImpl(authApi)

}