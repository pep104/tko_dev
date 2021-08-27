package pro.apir.tko.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import pro.apir.tko.presentation.utils.mapper.ContextHostMapper
import pro.apir.tko.presentation.utils.mapper.HostMapper
import javax.inject.Singleton

@Module
class AppModule(private val context: Context, private val application: Application) {

    @Singleton
    @Provides
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideApplication(): Application = application



    @Provides
    @Singleton
    fun provideHostMapper(
        context: Context,
    ): HostMapper {
        return ContextHostMapper(context)
    }


}
