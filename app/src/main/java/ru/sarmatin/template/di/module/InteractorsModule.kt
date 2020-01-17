package ru.sarmatin.template.di.module

import dagger.Module
import dagger.Provides
import ru.sarmatin.template.data.repository.example.ExampleRepository
import ru.sarmatin.template.domain.interactors.ExampleInteractor
import ru.sarmatin.template.domain.interactors.ExampleInteractorImpl
import javax.inject.Singleton

@Module
class InteractorsModule {

    @Provides
    @Singleton
    fun exampleInteractor(exampleRepository: ExampleRepository): ExampleInteractor = ExampleInteractorImpl(exampleRepository)

}