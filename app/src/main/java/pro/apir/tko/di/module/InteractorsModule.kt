package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.data.repository.auth.AuthRepository
import pro.apir.tko.data.repository.example.ExampleRepository
import pro.apir.tko.domain.interactors.ExampleInteractor
import pro.apir.tko.domain.interactors.ExampleInteractorImpl
import pro.apir.tko.domain.interactors.auth.AuthInteractor
import pro.apir.tko.domain.interactors.auth.AuthInteractorImpl
import javax.inject.Singleton

@Module
class InteractorsModule {

    @Provides
    @Singleton
    fun exampleInteractor(exampleRepository: ExampleRepository): ExampleInteractor = ExampleInteractorImpl(exampleRepository)

    @Provides
    @Singleton
    fun authInteractor(authRepository: AuthRepository): AuthInteractor = AuthInteractorImpl(authRepository)

}