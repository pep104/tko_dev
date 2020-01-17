package pro.apir.tko.di.module

import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.di.ViewModelKey
import pro.apir.tko.presentation.ui.main.GlobalState
import pro.apir.tko.presentation.ui.main.login.LoginViewModel
import pro.apir.tko.presentation.ui.main.menu.MenuViewModel
import pro.apir.tko.presentation.ui.main.splash.SplashViewModel

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

    @Binds
    @IntoMap
    @ViewModelKey(MenuViewModel::class)
    abstract fun bindMenuVM(menuViewModel: MenuViewModel): ViewModel

    //Stated

    @Binds
    @IntoMap
    @ViewModelKey(GlobalState::class)
    abstract fun bindGlobalStateFactory(f: GlobalState.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModelFactory(f: LoginViewModel.Factory): ViewModelAssistedFactory<out ViewModel>


}