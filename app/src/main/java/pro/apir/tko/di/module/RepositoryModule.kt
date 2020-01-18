package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.auth.AuthRepositoryImpl
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.data.repository.inventory.InventoryRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun authRepository(authApi: AuthApi, tokenManager: TokenManager): AuthRepository = AuthRepositoryImpl(authApi, tokenManager)

    @Provides
    @Singleton
    fun inventoryRepository(tokenManager: TokenManager, inventoryApi: InventoryApi): InventoryRepository = InventoryRepositoryImpl(tokenManager, inventoryApi)

}