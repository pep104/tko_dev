package pro.apir.tko.presentation.ui.main.inventory.list

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottomsheet_inventory_list.view.*
import kotlinx.android.synthetic.main.content_inventory_list.view.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.domain.model.ContainerAreaModel
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.inventory.detailed.InventoryDetailedFragment

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class InventoryListFragment : BaseFragment(), ContainerListAdapter.OnItemClickListener {

    private val viewModel: InventoryListViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_inventory_list

    override fun handleFailure() = viewModel.failure

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var bottomSheetLayout: ConstraintLayout

    private lateinit var loadingList: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var btnMenu: ImageView
    private lateinit var btnSearch: ImageView

    private lateinit var mapView: MapView


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

        btnMenu = view.btnMenu
        btnSearch = view.btnSearch

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        adapter = ContainerListAdapter().apply { setListener(this@InventoryListFragment) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)


        observeViewModel()

        Log.e("viewSize", bottomSheetLayout.height.toString())

        //todo
        mapView = view.map
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

    }

    override fun onResume() {
        super.onResume()
        //TODO REMOVE?
        viewModel.testGet()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        adapter.setListener(null)
        super.onDestroyView()
    }

    private fun observeViewModel() {
        viewModel.containers.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                adapter.setList(it)
                loadingList.goneWithFade()
            }
        })
    }

    override fun onItemClicked(item: ContainerAreaModel) {
        findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryDetailedFragment, bundleOf(InventoryDetailedFragment.KEY_ID to item.id, InventoryDetailedFragment.KEY_HEADER to item.location))
    }
}