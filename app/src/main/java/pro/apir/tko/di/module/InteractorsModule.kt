package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.data.repository.route.RouteRepository
import pro.apir.tko.data.repository.route.RouteSessionRepository
import pro.apir.tko.data.repository.route.photo.RoutePhotoRepository
import pro.apir.tko.data.repository.user.UserRepository
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.interactors.address.AddressInteractorImpl
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.domain.interactors.auth.AuthInteractorImpl
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.inventory.InventoryInteractorImpl
import pro.apir.tko.domain.interactors.route.RouteInteractor
import pro.apir.tko.domain.interactors.route.RouteInteractorImpl
import pro.apir.tko.domain.interactors.route.RouteSessionInteractor
import pro.apir.tko.domain.interactors.route.RouteSessionInteractorImpl
import pro.apir.tko.domain.interactors.route.photo.RoutePhotoInteractor
import pro.apir.tko.domain.interactors.route.photo.RoutePhotoInteractorImpl
import pro.apir.tko.domain.interactors.user.UserInteractor
import pro.apir.tko.domain.interactors.user.UserInteractorImpl
import javax.inject.Singleton

@Module
class InteractorsModule {


    @Provides
    @Singleton
    fun authInteractor(authRepository: AuthRepository, userRepository: UserRepository, tokenManager: TokenManager, preferencesManager: PreferencesManager): AuthInteractor = AuthInteractorImpl(authRepository, userRepository, tokenManager, preferencesManager)

    @Provides
    @Singleton
    fun inventoryInteractor(inventoryRepository: InventoryRepository, attachmentRepository: AttachmentRepository): InventoryInteractor = InventoryInteractorImpl(inventoryRepository, attachmentRepository)

    @Provides
    @Singleton
    fun routeInteractor(routeRepository: RouteRepository, routeSessionInteractor: RouteSessionInteractor): RouteInteractor = RouteInteractorImpl(routeRepository, routeSessionInteractor)

    @Provides
    @Singleton
    fun addressInteractor(addressRepository: AddressRepository): AddressInteractor = AddressInteractorImpl(addressRepository)

    @Provides
    @Singleton
    fun routeSessionInteractor(routeSessionRepository: RouteSessionRepository, userRepository: UserRepository): RouteSessionInteractor = RouteSessionInteractorImpl(routeSessionRepository, userRepository)

    @Provides
    @Singleton
    fun routePhotoInteractor(routePhotoRepository: RoutePhotoRepository): RoutePhotoInteractor = RoutePhotoInteractorImpl(routePhotoRepository)

    @Provides
    @Singleton
    fun userInteractor(userRepository: UserRepository): UserInteractor = UserInteractorImpl(userRepository)

}