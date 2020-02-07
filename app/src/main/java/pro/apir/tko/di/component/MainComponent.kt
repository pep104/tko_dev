package pro.apir.tko.di.component

import dagger.Subcomponent
import pro.apir.tko.di.module.PresentationModule
import pro.apir.tko.presentation.ui.main.address.AddressFragment
import pro.apir.tko.presentation.ui.main.camera.CameraFragment
import pro.apir.tko.presentation.ui.main.inventory.detailed.InventoryDetailedFragment
import pro.apir.tko.presentation.ui.main.inventory.edit.InventoryEditFragment
import pro.apir.tko.presentation.ui.main.list.MainListMapFragment
import pro.apir.tko.presentation.ui.main.login.LoginFragment
import pro.apir.tko.presentation.ui.main.menu.MenuFragment
import pro.apir.tko.presentation.ui.main.route.detailed.RouteDetailedFragment
import pro.apir.tko.presentation.ui.main.splash.SplashFragment

@Subcomponent(modules = [PresentationModule::class])
interface MainComponent {

    fun injectSplashFragment(fragment: SplashFragment)

    fun injectLoginFragment(fragment: LoginFragment)

    fun injectMenuFragment(fragment: MenuFragment)

    fun injectInventoryListFragment(fragment: MainListMapFragment)

    fun injectInventoryDetailedFragment(fragment: InventoryDetailedFragment)

    fun injectInventoryEditFragment(fragment: InventoryEditFragment)

    fun injectAddressFragment(fragment: AddressFragment)

    fun injectCameraFragment(fragment: CameraFragment)

    fun injectRouteDetailedFragment(fragment: RouteDetailedFragment)

}