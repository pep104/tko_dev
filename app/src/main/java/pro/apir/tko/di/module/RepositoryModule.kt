package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.cache.ContainerAreaListCache
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.framework.network.api.*
import pro.apir.tko.data.framework.room.dao.PhotoDao
import pro.apir.tko.data.framework.source.attachment.IAttachmentSource
import pro.apir.tko.data.mapper.TrackingFailureCodeMapper
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.data.repository.address.AddressRepositoryImpl
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.attachment.AttachmentRepositoryImpl
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.auth.AuthRepositoryImpl
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.data.repository.inventory.InventoryRepositoryImpl
import pro.apir.tko.data.repository.route.RouteRepository
import pro.apir.tko.data.repository.route.RouteRepositoryImpl
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.data.repository.route.RouteSessionRepositoryImpl
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepositoryImpl
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.data.repository.user.UserRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun authRepository(authApi: AuthApi, tokenManager: TokenManager): AuthRepository = AuthRepositoryImpl(authApi, tokenManager)

    @Provides
    @Singleton
    fun inventoryRepository(tokenManager: TokenManager, inventoryApi: InventoryApi, containerAreaListCache: ContainerAreaListCache): InventoryRepository = InventoryRepositoryImpl(tokenManager, inventoryApi, containerAreaListCache)

    @Provides
    @Singleton
    fun routeRepository(tokenManager: TokenManager, routeApi: RouteApi): RouteRepository = RouteRepositoryImpl(tokenManager, routeApi)

    @Provides
    @Singleton
    fun addressRepository(suggestionApi: SuggestionApi, suggestionDetailedApi: SuggestionDetailedApi): AddressRepository = AddressRepositoryImpl(suggestionApi, suggestionDetailedApi)

    @Provides
    @Singleton
    fun attachmentRepository(tokenManager: TokenManager, attachmentApi: IAttachmentSource): AttachmentRepository = AttachmentRepositoryImpl(tokenManager, attachmentApi)

    @Provides
    @Singleton
    fun routeSessionRepository(
            routePhotoRepository: RoutePhotoRepository,
            routeRepository: RouteRepository,
            routeTrackApi: RouteTrackApi,
            userRepository: UserRepository,
            trackingFailureCodeMapper: TrackingFailureCodeMapper,
            tokenManager: TokenManager
    ): RouteSessionRepository = RouteSessionRepositoryImpl(
            routePhotoRepository,
            routeRepository,
            routeTrackApi,
            userRepository,
            trackingFailureCodeMapper,
            tokenManager
    )

    @Provides
    @Singleton
    fun routePhotoRepository(photoDao: PhotoDao): RoutePhotoRepository = RoutePhotoRepositoryImpl(photoDao)

    @Provides
    @Singleton
    fun userRepository(tokenManager: TokenManager, userApi: UserApi, preferencesManager: PreferencesManager): UserRepository = UserRepositoryImpl(tokenManager, userApi, preferencesManager)

    //Cache

    @Provides
    @Singleton
    fun cacheContainerShortModel(): ContainerAreaListCache = ContainerAreaListCache()

}