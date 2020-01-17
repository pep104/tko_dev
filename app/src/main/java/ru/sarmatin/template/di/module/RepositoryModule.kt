package ru.sarmatin.template.di.module

import dagger.Module
import dagger.Provides
import ru.sarmatin.template.data.framework.network.api.ExampleApi
import ru.sarmatin.template.data.framework.source.example.ExampleSource
import ru.sarmatin.template.data.repository.example.ExampleRepository
import ru.sarmatin.template.data.repository.example.ExampleRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun exampleRepository(exampleSource: ExampleSource): ExampleRepository = ExampleRepositoryImpl(exampleSource)

}