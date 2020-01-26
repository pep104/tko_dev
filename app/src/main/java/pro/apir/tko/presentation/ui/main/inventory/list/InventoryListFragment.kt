package pro.apir.tko.presentation.ui.main.inventory.list

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottomsheet_inventory_list.view.*
import kotlinx.android.synthetic.main.content_inventory_list.view.*
import kotlinx.android.synthetic.main.fragment_address.*
import kotlinx.android.synthetic.main.fragment_inventory_list.view.*
import kotlinx.coroutines.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.extension.addViewObserver
import pro.apir.tko.presentation.extension.dpToPx
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
    private lateinit var contentLayout: ConstraintLayout

    private lateinit var loadingList: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var btnAdd: MaterialButton
    private lateinit var btnMenu: ImageView
    private lateinit var btnSearch: ImageView

    private lateinit var mapView: MapView
    private var mapJob: Job? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private lateinit var adapter: ContainerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryListFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetLayout = view.bottomsheetInventory
        contentLayout = view.contentInventoryList

        recyclerView = view.recyclerView
        loadingList = view.loadingList

        btnAdd = view.btnAdd
        btnMenu = view.btnMenu
        btnSearch = view.btnSearch

        mapView = view.map

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

        btnAdd.setOnClickListener { findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryEditFragment) }

        setMap(mapView)

        observeViewModel()

        bottomSheetLayout.addViewObserver {
            contentLayout.layoutParams.height = bottomSheetLayout.y.toInt() + 16.dpToPx
            contentLayout.requestLayout()
        }

    }

    override fun onResume() {
        super.onResume()
        //TODO REMOVE?
        viewModel.testGet()
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()

        viewModel.zoomLevel?.let {
            mapView.controller.zoomTo(it, 0L)
        }
    }


    override fun onPause() {
        mapView.onPause()
        myLocationOverlay?.disableMyLocation()
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

                setMarkers(it)
            }
        })
    }

    private fun setMap(mapView: MapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.zoomTo(12.0, 0L)

        val locationProvider = GpsMyLocationProvider(context)
        myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)
        myLocationOverlay?.enableFollowLocation()

        mapView.overlayManager.add(myLocationOverlay)

        mapView.addMapListener(DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val map = mapView.mapCenter
                val box = mapView.boundingBox
                Log.e("mapCallback", "scroll ${map.latitude}, ${map.longitude}")
                Log.e("mapCallback", "scroll ${box.lonWest}, ${box.latSouth} : ${box.lonEast}, ${box.latNorth}")
                //todo save to vm
                //todo fetch data
                viewModel.fetch(box.lonWest, box.latSouth, box.lonEast, box.latNorth)
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                val map = mapView.mapCenter
                Log.e("mapCallback", "zoom ${map.latitude}, ${map.longitude}")
                viewModel.setZoomLevel(mapView.zoomLevelDouble)
                return false
            }
        }))

    }

    //TODO to vm and background task
    private fun setMarkers(list: List<ContainerAreaListModel>) {
        val markers = arrayListOf<Marker>()
        mapJob?.cancel()
        mapJob = CoroutineScope(Dispatchers.IO).launch {
            list.forEach {
                val coordinates = it.coordinates
                if (coordinates != null
                        && coordinates.lat != 0.0 && coordinates.lng != 0.0
                        && coordinates.lat in -85.05..85.05) {
                    val location = GeoPoint(coordinates.lat, coordinates.lng)
                    val marker = Marker(mapView)
                    marker.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_map_marker_circle)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    marker.position = location
                    markers.add(marker)

                }
            }
            withContext(Dispatchers.Main) {
                mapView.overlays.clear()
                mapView.overlays.addAll(markers)
                mapView.overlayManager.add(myLocationOverlay)
            }
        }

    }

    override fun onItemClicked(item: ContainerAreaListModel) {
        findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryDetailedFragment,
                bundleOf(
                        InventoryDetailedFragment.KEY_ID to item.id,
                        InventoryDetailedFragment.KEY_HEADER to item.location,
                        InventoryDetailedFragment.KEY_COORDINATES to item.coordinates
                ))
    }
}