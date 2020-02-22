package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.data.repository.attachment.AttachmentRepository
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.data.repository.route.RouteRepository
import pro.apir.tko.data.repository.route.RouteSessionRepository
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
import pro.apir.tko.domain.interactors.user.UserInteractor
import pro.apir.tko.domain.interactors.user.UserInteractorImpl
import javax.inject.Singleton

@Module
class InteractorsModule {


    @Provides
    @Singleton
    fun authInteractor(authRepository: AuthRepository): AuthInteractor = AuthInteractorImpl(authRepository)

    @Provides
    @Singleton
    fun inventoryInteractor(inventoryRepository: InventoryRepository, attachmentRepository: AttachmentRepository): InventoryInteractor = InventoryInteractorImpl(inventoryRepository, attachmentRepository)

    @Provides
    @Singleton
    fun routeInteractor(routeRepository: RouteRepository): RouteInteractor = RouteInteractorImpl(routeRepository)

    @Provides
    @Singleton
    fun addressInteractor(addressRepository: AddressRepository): AddressInteractor = AddressInteractorImpl(addressRepository)

    @Provides
    @Singleton
    fun routeSessionInteractor(routeSessionRepository: RouteSessionRepository): RouteSessionInteractor = RouteSessionInteractorImpl(routeSessionRepository)

    @Provides
    @Singleton
    fun userInteractor(userRepository: UserRepository): UserInteractor = UserInteractorImpl(userRepository)

}