package pro.apir.tko.presentation.ui.main.route.navigation

import android.os.Bundle
import androidx.fragment.app.viewModels
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 10.02.2020
 * Project: tko-android
 *
 */
class RouteNavigationFragment : BaseFragment() {

    private val viewModel: RouteNavigationViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_route_navigation

    override fun handleFailure() = viewModel.failure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteNavigationFragment(this)
    }

}