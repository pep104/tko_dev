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
import pro.apir.tko.presentation.ui.main.address.AddressViewModel
import pro.apir.tko.presentation.ui.main.camera.CameraViewModel
import pro.apir.tko.presentation.ui.main.inventory.detailed.InventoryDetailedViewModel
import pro.apir.tko.presentation.ui.main.inventory.edit.InventoryEditViewModel
import pro.apir.tko.presentation.ui.main.list.inventory.InventoryListViewModel
import pro.apir.tko.presentation.ui.main.list.route.RouteListViewModel
import pro.apir.tko.presentation.ui.main.login.LoginViewModel
import pro.apir.tko.presentation.ui.main.menu.MenuViewModel
import pro.apir.tko.presentation.ui.main.route.RouteDetailedViewModel
import pro.apir.tko.presentation.ui.main.route.navigation.RouteNavigationViewModel
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

    @Binds
    @IntoMap
    @ViewModelKey(InventoryListViewModel::class)
    abstract fun bindInvenotryListViewModelFactory(f: InventoryListViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(InventoryDetailedViewModel::class)
    abstract fun bindInventoryDetailedViewModelFactory(f: InventoryDetailedViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(InventoryEditViewModel::class)
    abstract fun bindInventoryEditViewModelFactory(f: InventoryEditViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(AddressViewModel::class)
    abstract fun bindAddressViewModelFactory(f: AddressViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindCameraViewModelFactory(f: CameraViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(RouteListViewModel::class)
    abstract fun bindRouteListViewModelFactory(f: RouteListViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(RouteDetailedViewModel::class)
    abstract fun bindRouteDetailedViewModelFactory(f: RouteDetailedViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(RouteNavigationViewModel::class)
    abstract fun bindRouteNavigationViewModelFactory(f: RouteNavigationViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

}