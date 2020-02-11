package pro.apir.tko.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context, private val application: Application) {

    @Singleton
    @Provides
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideApplication(): Application = application

}
