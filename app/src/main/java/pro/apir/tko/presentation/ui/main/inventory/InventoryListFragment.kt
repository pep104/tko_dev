package pro.apir.tko.presentation.ui.main.inventory

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottomsheet_inventory_list.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class InventoryListFragment : BaseFragment() {

    private val viewModel: InventoryListViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_inventory_list

    override fun handleFailure() = viewModel.failure

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var bottomSheetLayout: ConstraintLayout

    private lateinit var loadingList: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: ContainerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryListFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetLayout = view.bottomsheetInventory

        recyclerView = view.recyclerView
        loadingList = view.loadingList

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        adapter = ContainerListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)



        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        //TODO REMOVE?
        viewModel.testGet()
    }

    private fun observeViewModel() {
        viewModel.containers.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                adapter.setList(it)
                loadingList.goneWithFade()
            }
        })
    }

}