package pro.apir.tko.presentation.ui.main.route.detailed

import android.os.Bundle
import androidx.fragment.app.viewModels
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
class RouteDetailedFragment : BaseFragment() {

    private val viewModel: RouteDetailedViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_route_detailed

    override fun handleFailure() = viewModel.failure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteDetailedFragment(this)
    }

}