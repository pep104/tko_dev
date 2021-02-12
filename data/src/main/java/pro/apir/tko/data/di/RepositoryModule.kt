package pro.apir.tko.data.di

import dagger.Binds
import dagger.Module
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
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun authRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun inventoryRepository(impl: InventoryRepositoryImpl): InventoryRepository

    @Binds
    @Singleton
    abstract fun routeRepository(impl: RouteRepositoryImpl): RouteRepository

    @Binds
    @Singleton
    abstract fun addressRepository(impl: AddressRepositoryImpl): AddressRepository

    @Binds
    @Singleton
    abstract fun attachmentRepository(impl: AttachmentRepositoryImpl): AttachmentRepository

    @Binds
    @Singleton
    abstract fun routeSessionRepository(impl: RouteSessionRepositoryImpl): RouteSessionRepository

    @Binds
    @Singleton
    abstract fun routePhotoRepository(impl: RoutePhotoRepositoryImpl): RoutePhotoRepository

    @Binds
    @Singleton
    abstract fun userRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun credentialsRepository(impl: CredentialsRepositoryImpl): CredentialsRepository

    //Cache

//    @Binds
//    @Singleton
//    abstract fun cacheContainerShortModel(impl: ContainerAreaListCache): ContainerAreaListCache

}