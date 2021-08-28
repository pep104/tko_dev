package pro.apir.tko.domain.di

import dagger.Binds
import dagger.Module
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.interactors.address.AddressInteractorImpl
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.domain.interactors.auth.AuthInteractorImpl
import pro.apir.tko.domain.interactors.blocked.BlockedInteractor
import pro.apir.tko.domain.interactors.blocked.BlockedInteractorImpl
import pro.apir.tko.domain.interactors.host.HostInteractor
import pro.apir.tko.domain.interactors.host.HostInteractorImpl
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
import javax.inject.Singleton

@Module
abstract class InteractorsModule {


    @Binds
    @Singleton
    abstract fun authInteractor(authInteractorImpl: AuthInteractorImpl): AuthInteractor

    @Binds
    @Singleton
    abstract fun inventoryInteractor(interactorImpl: InventoryInteractorImpl): InventoryInteractor

    @Binds
    @Singleton
    abstract fun routeInteractor(routeInteractorImpl: RouteInteractorImpl): RouteInteractor

    @Binds
    @Singleton
    abstract fun addressInteractor(addressInteractorImpl: AddressInteractorImpl): AddressInteractor

    @Binds
    @Singleton
    abstract fun routeSessionInteractor(routeSessionInteractorImpl: RouteSessionInteractorImpl): RouteSessionInteractor

    @Binds
    @Singleton
    abstract fun routePhotoInteractor(routePhotoInteractorImpl: RoutePhotoInteractorImpl): RoutePhotoInteractor

    @Binds
    @Singleton
    abstract fun userInteractor(userInteractor: UserInteractorImpl): UserInteractor

    @Binds
    @Singleton
    abstract fun hostInteractor(hostInteractorImpl: HostInteractorImpl): HostInteractor

    @Binds
    @Singleton
    abstract fun blockedInteractor(blockedInteractor: BlockedInteractorImpl): BlockedInteractor

}