package pro.apir.tko.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pro.apir.tko.presentation.utils.mapper.ContextHostMapper
import pro.apir.tko.presentation.utils.mapper.HostMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideHostMapper(
        @ApplicationContext context: Context
    ): HostMapper {
        return ContextHostMapper(context)
    }


}
