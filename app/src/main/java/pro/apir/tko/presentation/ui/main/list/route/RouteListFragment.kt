package pro.apir.tko.presentation.ui.main.list.route

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import pro.apir.tko.R
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.ui.main.list.BaseListFragment

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

    private val routesListObserver = Observer<List<Any>> {
        it?.let { list ->
            loadingList.goneWithFade()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteListFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRouteType()
        observeViewModel()
        listAdapter = RouteListAdapter().apply { setListener(this@RouteListFragment) }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun observeViewModel(){

    }


    fun setRouteType() {
        textBottomHeader.text = getString(R.string.text_route_list_header)
        btnAction.text = getString(R.string.btn_choose_route)
        btnAction.setOnClickListener {
            //todo to route
        }
//        viewModel.routes.observe(viewLifecycleOwner, routesListObserver)
    }

    override fun onRouteChosen(item: RouteModel) {

    }
}