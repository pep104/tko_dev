package ru.sarmatin.template.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import ru.sarmatin.template.di.ViewModelAssistedFactory
import ru.sarmatin.template.di.ViewModelFactory
import ru.sarmatin.template.di.ViewModelKey
import ru.sarmatin.template.presentation.ui.main.GlobalState
import ru.sarmatin.template.presentation.ui.main.example.ExampleStateViewModel
import ru.sarmatin.template.presentation.ui.main.splash.SplashViewModel

@AssistedModule
@Module(includes = [AssistedInject_ViewModelModule::class])
abstract class ViewModelModule {

    @Multibinds
    abstract fun viewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards ViewModel>

    @Multibinds
    abstract fun assistedViewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards ViewModelAssistedFactory<out ViewModel>>

    //Default

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashVM(splashViewModel: SplashViewModel): ViewModel

    //Stated

    @Binds
    @IntoMap
    @ViewModelKey(GlobalState::class)
    abstract fun bindGlobalStateFactory(f: GlobalState.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(ExampleStateViewModel::class)
    abstract fun bindExampleSVMFactory(f: ExampleStateViewModel.Factory): ViewModelAssistedFactory<out ViewModel>


}