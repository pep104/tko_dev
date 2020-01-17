package pro.apir.tko.di.component

import dagger.Subcomponent
import pro.apir.tko.di.module.PresentationModule
import pro.apir.tko.presentation.ui.main.login.LoginFragment
import pro.apir.tko.presentation.ui.main.login.LoginViewModel
import pro.apir.tko.presentation.ui.main.menu.MenuFragment
import pro.apir.tko.presentation.ui.main.splash.SplashFragment

@Subcomponent(modules = [PresentationModule::class])
interface MainComponent {

    fun injectSplashFragment(fragment: SplashFragment)

    fun injectLoginFragment(fragment: LoginFragment)

    fun injectMenuFragment(fragment: MenuFragment)

}