package pro.apir.tko.presentation.ui.main.list.route

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import pro.apir.tko.R
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.dict.OptionsDictionariesManager
import pro.apir.tko.presentation.extension.gone
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.extension.visible
import pro.apir.tko.presentation.ui.main.list.BaseListFragment
import pro.apir.tko.presentation.ui.main.route.detailed.RouteDetailedFragment
import pro.apir.tko.presentation.utils.PaginationScrollListener
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 08.02.2020
 * Project: tko-android
+ */
class RouteListFragment : BaseListFragment(), RouteListAdapter.RouteChooseListener {

    @Inject
    lateinit var optionsDictionariesManager: OptionsDictionariesManager

    private val viewModel: RouteListViewModel by viewModels()

    override fun viewModel() = viewModel

    override fun handleFailure() = viewModel.failure

    private lateinit var listAdapter: RouteListAdapter

    private val routesListObserver = Observer<List<RouteModel>> {
        it?.let { list ->
            listAdapter.setList(list)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteListFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRouteType()

        val layoutManager = LinearLayoutManager(context)
        listAdapter = RouteListAdapter(optionsDictionariesManager).apply {
            setListener(this@RouteListFragment)
            setChosen(viewModel.choosenRoute.value?.id)
        }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager, viewModel.pageSize) {
            override fun loadMoreItems() = viewModel.fetchMore()
            override fun isLastPage() = viewModel.isLastPage
            override fun isPageLoading() = viewModel.isPageLoading
        })

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.routes.observe(viewLifecycleOwner, routesListObserver)
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                loadingList.visible()
            } else {
                loadingList.goneWithFade()
            }
        })

        viewModel.choosenRoute.observe(viewLifecycleOwner, Observer { model ->
            if (model != null) {
                btnAction.visible()
                btnAction.setOnClickListener { _ ->
                    findNavController().navigate(R.id.action_routeListFragment_to_graphRoute, bundleOf(RouteDetailedFragment.KEY_ROUTE to model))
                }
            } else {
                btnAction.gone()
                btnAction.setOnClickListener(null)
            }
        })
    }


    fun setRouteType() {
        textBottomHeader.text = getString(R.string.text_route_list_header)
        btnAction.text = getString(R.string.btn_choose_route)
    }

    override fun onRouteChosen(itemID: Int?) {
        viewModel.setChosenRoute(itemID)
    }
}