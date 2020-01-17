package pro.apir.tko.di.component

import dagger.Component
import pro.apir.tko.di.module.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, ViewModelModule::class, InteractorsModule::class, RepositoryModule::class, FrameworkModule::class]
)
interface AppComponent {

    fun createMainComponent(): MainComponent

}