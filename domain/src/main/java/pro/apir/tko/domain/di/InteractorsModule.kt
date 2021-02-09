package pro.apir.tko.domain.di

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.framework.manager.preferences.PreferencesManager
import pro.apir.tko.data.framework.manager.token.TokenManager
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.interactors.address.AddressInteractorImpl
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.domain.interactors.auth.AuthInteractorImpl
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.inventory.InventoryInteractorImpl
import pro.apir.tko.domain.interactors.route.RouteInteractor
import pro.apir.tko.domain.interactors.route.RouteInteractorImpl
import pro.apir.tko.domain.interactors.route.photo.RoutePhotoInteractor
import pro.apir.tko.domain.interactors.route.photo.RoutePhotoInteractorImpl
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractor
import pro.apir.tko.domain.interactors.route.session.RouteSessionInteractorImpl
import pro.apir.tko.domain.interactors.user.UserInteractor
import pro.apir.tko.domain.interactors.user.UserInteractorImpl
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
class InteractorsModule {


    @Provides
    @Singleton
    fun authInteractor(authRepository: AuthRepository, userRepository: UserRepository, credentialsRepository: CredentialsRepository): AuthInteractor = AuthInteractorImpl(authRepository, userRepository, credentialsRepository)

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
    fun routeSessionInteractor(routeSessionRepository: RouteSessionRepository, routePhotoRepository: RoutePhotoRepository, attachmentRepository: AttachmentRepository, userRepository: UserRepository): RouteSessionInteractor = RouteSessionInteractorImpl(routeSessionRepository, routePhotoRepository, attachmentRepository, userRepository)

    @Provides
    @Singleton
    fun routePhotoInteractor(routePhotoRepository: RoutePhotoRepository, routeSessionInteractor: RouteSessionInteractor): RoutePhotoInteractor = RoutePhotoInteractorImpl(routePhotoRepository, routeSessionInteractor)

    @Provides
    @Singleton
    fun userInteractor(userRepository: UserRepository): UserInteractor = UserInteractorImpl(userRepository)

}