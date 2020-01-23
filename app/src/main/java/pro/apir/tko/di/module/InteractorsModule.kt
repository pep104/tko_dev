package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.repository.address.AddressRepository
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.inventory.InventoryRepository
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.domain.interactors.address.AddressInteractorImpl
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.domain.interactors.auth.AuthInteractorImpl
import pro.apir.tko.domain.interactors.inventory.InventoryInteractor
import pro.apir.tko.domain.interactors.inventory.InventoryInteractorImpl
import javax.inject.Singleton

@Module
class InteractorsModule {


    @Provides
    @Singleton
    fun authInteractor(authRepository: AuthRepository): AuthInteractor = AuthInteractorImpl(authRepository)

    @Provides
    @Singleton
    fun inventoryInteractor(inventoryRepository: InventoryRepository): InventoryInteractor = InventoryInteractorImpl(inventoryRepository)

    @Provides
    @Singleton
    fun addressInteractor(addressRepository: AddressRepository): AddressInteractor = AddressInteractorImpl(addressRepository)

}