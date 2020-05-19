package pro.apir.tko.presentation.ui.main.list.inventory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.ui.main.inventory.detailed.InventoryDetailedFragment
import pro.apir.tko.presentation.ui.main.inventory.edit.InventoryEditListSharedViewModel
import pro.apir.tko.presentation.ui.main.list.BaseListFragment
import ru.sarmatin.mobble.utils.consumablelivedata.ConsumableObserver

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class InventoryListFragment : BaseListFragment(), ContainerListAdapter.OnItemClickListener {

    private val viewModel: InventoryListViewModel by viewModels()
    private val sharedEditListViewModel: InventoryEditListSharedViewModel by activityViewModels()

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

        viewModel.containers.observe(viewLifecycleOwner, containersListObserver)

        viewModel.searchContainersResults.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {

            } else {
                viewModel.containers.removeObserver(containersListObserver)
                viewModel.searchContainersResults.observe(viewLifecycleOwner, containersListObserver)
            }
        })

        viewModel.searchMode.observe(viewLifecycleOwner, Observer {
            if (it) {

            } else {
                viewModel.searchContainersResults.removeObserver(containersListObserver)
                viewModel.containers.observe(viewLifecycleOwner, containersListObserver)
            }
        })

        //EDIT or CREATE result
        sharedEditListViewModel.resultEvent.observe(
                viewLifecycleOwner,
                ConsumableObserver
                {
                    viewModel.handleEditResult(it)
                }
        )

    }


    fun setInventoryType() {
        textBottomHeader.text = getString(R.string.text_inventory_list_header)
        btnAction.text = getString(R.string.btn_add_waste_are)
        btnAction.setOnClickListener { findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryEditFragment) }
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