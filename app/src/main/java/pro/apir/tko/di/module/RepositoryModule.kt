package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.auth.AuthRepositoryImpl

import javax.inject.Singleton

@Module
class RepositoryModule {


    @Provides
    @Singleton
    fun authRepository(authApi: AuthApi, tokenManager: TokenManager): AuthRepository = AuthRepositoryImpl(authApi, tokenManager)

}