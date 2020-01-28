package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.AuthApi
import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.framework.network.api.SuggestionApi
import pro.apir.tko.data.framework.network.api.SuggestionDetailedApi
import pro.apir.tko.data.framework.source.attachment.IAttachmentSource
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.data.repository.address.AddressRepositoryImpl
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.attachment.AttachmentRepositoryImpl
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

    @Provides
    @Singleton
    fun addressRepository(suggestionApi: SuggestionApi, suggestionDetailedApi: SuggestionDetailedApi): AddressRepository = AddressRepositoryImpl(suggestionApi, suggestionDetailedApi)

    @Provides
    @Singleton
    fun attachmentRepository(tokenManager: TokenManager, attachmentApi: IAttachmentSource): AttachmentRepository = AttachmentRepositoryImpl(tokenManager, attachmentApi)

}