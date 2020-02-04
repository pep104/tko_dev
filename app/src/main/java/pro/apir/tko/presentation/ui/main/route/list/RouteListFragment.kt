package pro.apir.tko.presentation.ui.main.route.list

import android.os.Bundle
import androidx.fragment.app.viewModels
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
class RouteListFragment : BaseFragment() {

    private val viewModel: RouteListViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_route_list

    override fun handleFailure() = viewModel.failure


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteListFragment(this)
    }

}