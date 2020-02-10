package pro.apir.tko.presentation.ui.main.list.inventory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.ui.main.inventory.detailed.InventoryDetailedFragment
import pro.apir.tko.presentation.ui.main.list.BaseListFragment

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class InventoryListFragment : BaseListFragment(), ContainerListAdapter.OnItemClickListener {

    private val viewModel: InventoryListViewModel by viewModels()

    override fun viewModel() = viewModel

    override fun handleFailure() = viewModel.failure

    private lateinit var containerListAdapter: ContainerListAdapter


    private val containersListObserver = Observer<List<ContainerAreaListModel>> {
        it?.let { list ->
            containerListAdapter.setList(list)
            loadingList.goneWithFade()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryListFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        containerListAdapter = ContainerListAdapter().apply { setListener(this@InventoryListFragment) }
        recyclerView.adapter = containerListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        setInventoryType()
        observeViewModel()
    }

    override fun onDestroyView() {
        containerListAdapter.setListener(null)
        super.onDestroyView()
    }

    private fun observeViewModel() {

    }


    fun setInventoryType() {
        textBottomHeader.text = getString(R.string.text_inventory_list_header)
        btnAction.text = getString(R.string.btn_add_container)
        btnAction.setOnClickListener { findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryEditFragment) }
        viewModel.containers.observe(viewLifecycleOwner, containersListObserver)
    }


    override fun onContainerItemClicked(item: ContainerAreaListModel) {
        findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryDetailedFragment,
                bundleOf(
                        InventoryDetailedFragment.KEY_ID to item.id,
                        InventoryDetailedFragment.KEY_HEADER to item.location,
                        InventoryDetailedFragment.KEY_COORDINATES to item.coordinates
                ))
    }

}