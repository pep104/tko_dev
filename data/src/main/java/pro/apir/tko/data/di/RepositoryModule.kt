package pro.apir.tko.data.di

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.cache.ContainerAreaListCache
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.CredentialsManager
import pro.apir.tko.data.framework.network.api.*
import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.source.attachment.IAttachmentSource
import pro.apir.tko.data.mapper.TrackingFailureCodeMapper
import pro.apir.tko.data.repository.address.AddressRepositoryImpl
import pro.apir.tko.data.repository.attachment.AttachmentRepositoryImpl
import pro.apir.tko.data.repository.auth.AuthRepositoryImpl
import pro.apir.tko.data.repository.credentials.CredentialsRepositoryImpl
import pro.apir.tko.data.repository.inventory.InventoryRepositoryImpl
import pro.apir.tko.data.repository.route.RouteRepositoryImpl
import pro.apir.tko.data.repository.route.RouteSessionRepositoryImpl
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepositoryImpl
import pro.apir.tko.data.repository.user.UserRepositoryImpl
import pro.apir.tko.domain.repository.address.AddressRepository
import pro.apir.tko.domain.repository.attachment.AttachmentRepository
import pro.apir.tko.domain.repository.auth.AuthRepository
import pro.apir.tko.domain.repository.credentials.CredentialsRepository
import pro.apir.tko.domain.repository.inventory.InventoryRepository
import pro.apir.tko.domain.repository.route.RouteRepository
import pro.apir.tko.domain.repository.route.RouteSessionRepository
import pro.apir.tko.domain.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.domain.repository.user.UserRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun authRepository(authApi: AuthApi, credentialsManager: CredentialsManager): AuthRepository = AuthRepositoryImpl(authApi, credentialsManager)

    @Provides
    @Singleton
    fun inventoryRepository(credentialsManager: CredentialsManager, inventoryApi: InventoryApi, containerAreaListCache: ContainerAreaListCache): InventoryRepository = InventoryRepositoryImpl(credentialsManager, inventoryApi, containerAreaListCache)

    @Provides
    @Singleton
    fun routeRepository(credentialsManager: CredentialsManager, routeApi: RouteApi): RouteRepository = RouteRepositoryImpl(credentialsManager, routeApi)

    @Provides
    @Singleton
    fun addressRepository(suggestionApi: SuggestionApi, suggestionDetailedApi: SuggestionDetailedApi): AddressRepository = AddressRepositoryImpl(suggestionApi, suggestionDetailedApi)

    @Provides
    @Singleton
    fun attachmentRepository(credentialsManager: CredentialsManager, attachmentApi: IAttachmentSource): AttachmentRepository = AttachmentRepositoryImpl(credentialsManager, attachmentApi)

    @Provides
    @Singleton
    fun routeSessionRepository(
            routePhotoRepository: RoutePhotoRepository,
            routeRepository: RouteRepository,
            routeTrackApi: RouteTrackApi,
            userRepository: UserRepository,
            trackingFailureCodeMapper: TrackingFailureCodeMapper,
            credentialsManager: CredentialsManager
    ): RouteSessionRepository = RouteSessionRepositoryImpl(
            routePhotoRepository,
            routeRepository,
            routeTrackApi,
            userRepository,
            trackingFailureCodeMapper,
            credentialsManager
    )

    @Provides
    @Singleton
    fun routePhotoRepository(photoDao: PhotoDao): RoutePhotoRepository = RoutePhotoRepositoryImpl(photoDao)

    @Provides
    @Singleton
    fun userRepository(credentialsManager: CredentialsManager, userApi: UserApi, preferencesManager: PreferencesManager): UserRepository = UserRepositoryImpl(credentialsManager, userApi, preferencesManager)

    @Provides
    @Singleton
    fun credentialsRepository(): CredentialsRepository = CredentialsRepositoryImpl()

    //Cache

    @Provides
    @Singleton
    fun cacheContainerShortModel(): ContainerAreaListCache = ContainerAreaListCache()

}