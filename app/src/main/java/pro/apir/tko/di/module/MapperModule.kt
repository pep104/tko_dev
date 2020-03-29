package pro.apir.tko.di.module

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
@Module
class MapperModule {

    @Singleton
    @Provides
    fun photoTypeMapper(): PhotoTypeMapper = PhotoTypeMapperImpl()


}