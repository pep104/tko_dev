package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import pro.apir.tko.presentation.dict.OptionsDictionariesManager
import pro.apir.tko.presentation.dict.OptionsDictionariesManagerImpl
import javax.inject.Singleton

@Module
class PresentationModule {

    @Provides
    @Singleton
    fun provideDictManager(): OptionsDictionariesManager = OptionsDictionariesManagerImpl()

}