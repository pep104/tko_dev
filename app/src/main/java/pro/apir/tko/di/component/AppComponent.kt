package pro.apir.tko.di.component

import dagger.Component
import pro.apir.tko.data.di.FrameworkModule
import pro.apir.tko.data.di.MapperModule
import pro.apir.tko.data.di.RepositoryModule
import pro.apir.tko.di.module.AppModule
import pro.apir.tko.di.module.ViewModelModule
import pro.apir.tko.domain.di.InteractorsModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AppModule::class,
            ViewModelModule::class,
            InteractorsModule::class,
            RepositoryModule::class,
            FrameworkModule::class,
            MapperModule::class
        ]
)
interface AppComponent {

    fun createMainComponent(): MainComponent

}