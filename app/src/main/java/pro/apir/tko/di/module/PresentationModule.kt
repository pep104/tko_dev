package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.apir.tko.domain.interactors.address.AddressInteractor
import pro.apir.tko.presentation.utils.address.AddressSuggestionRequester

@Module
@InstallIn(SingletonComponent::class)
class PresentationModule {

    @Provides
    fun provideAddressSuggestionRequester(
        addressInteractor: AddressInteractor
    ): AddressSuggestionRequester {
        return AddressSuggestionRequester(
            addressInteractor
        )
    }

}