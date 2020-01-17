package ru.sarmatin.template.di.component

import dagger.Subcomponent
import ru.sarmatin.template.di.module.PresentationModule
import ru.sarmatin.template.presentation.ui.main.splash.SplashFragment

@Subcomponent(modules = [PresentationModule::class])
interface MainComponent {

    fun injectSplashFragment(fragment: SplashFragment)

}