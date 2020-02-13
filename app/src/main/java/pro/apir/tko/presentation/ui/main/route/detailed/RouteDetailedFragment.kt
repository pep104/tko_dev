package pro.apir.tko.presentation.ui.main.route.detailed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottomsheet_route_detailed.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.RouteModel
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var stopAdapter: RouteStopPointsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteDetailedFragment(this)

        arguments?.let {
            val route: RouteModel? = it.getParcelable(KEY_ROUTE)
            if (route != null)
                viewModel.init(route)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.recyclerView
        stopAdapter = RouteStopPointsAdapter()

        with(recyclerView) {
            adapter = stopAdapter
            layoutManager = LinearLayoutManager(context)
        }

        observeViewModel()
    }

    private fun observeViewModel() {

        viewModel.routeStops.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                stopAdapter.setList(list)
            }

        })

    }

    companion object {

        const val KEY_ROUTE = "route"

    }

}