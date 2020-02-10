package pro.apir.tko.presentation.ui.main.list.route

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import pro.apir.tko.R
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.extension.visible
import pro.apir.tko.presentation.ui.main.list.BaseListFragment
import pro.apir.tko.presentation.utils.PaginationScrollListener

/**
 * Created by Антон Сарматин
 * Date: 08.02.2020
 * Project: tko-android
+ */
class RouteListFragment : BaseListFragment(), RouteListAdapter.RouteChooseListener {

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
        listAdapter = RouteListAdapter().apply { setListener(this@RouteListFragment) }
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
    }


    fun setRouteType() {
        textBottomHeader.text = getString(R.string.text_route_list_header)
        btnAction.text = getString(R.string.btn_choose_route)
        btnAction.setOnClickListener {
            //todo to route
        }

    }

    override fun onRouteChosen(item: RouteModel) {

    }
}