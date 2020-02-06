package pro.apir.tko.presentation.ui.main.inventory.list

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottomsheet_inventory_list.view.*
import kotlinx.android.synthetic.main.content_map.view.*
import kotlinx.android.synthetic.main.fragment_inventory_list.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.extension.*
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

    private lateinit var layoutSearch: ConstraintLayout
    private lateinit var etSearch: EditText

    private lateinit var loadingList: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContainerListAdapter

    private lateinit var btnAdd: MaterialButton
    private lateinit var btnMenu: ImageView
    private lateinit var btnSearch: ImageView

    private lateinit var mapView: MapView
    private var mapJob: Job? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryListFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetLayout = view.bottomsheetInventory
        contentLayout = view.contentMap

        layoutSearch = view.layoutSearch
        etSearch = view.etSearch

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
        btnSearch.setOnClickListener { viewModel.switchSearchMode() }

        setMap(mapView)
        observeViewModel()

        bottomSheetLayout.addViewObserver {
            contentLayout.layoutParams.height = bottomSheetLayout.y.toInt() + 16.dpToPx
            contentLayout.requestLayout()
        }

    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()

        viewModel.zoomLevel?.let {
            mapView.controller.zoomTo(it, 0L)
        }

        viewModel.lastPosition?.let {
            mapView.controller.setCenter(it)
        }
    }


    override fun onPause() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        hideKeyboard()
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

        viewModel.searchMode.observe(viewLifecycleOwner, Observer {
            layoutSearch.isInvisible = !it
            if(it){
                etSearch.focusWithKeyboard()
            }else{
                hideKeyboard()
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
//        myLocationOverlay?.setDirectionArrow(ContextCompat.getDrawable(context!!, R.drawable.ic_map_user_location)?.toBitmap(), ContextCompat.getDrawable(context!!, R.drawable.ic_map_user_location)?.toBitmap())
        if (viewModel.lastPosition == null) {
            myLocationOverlay?.enableFollowLocation()
        }
        mapView.overlayManager.add(myLocationOverlay)

        mapView.addMapListener(DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val box = mapView.boundingBox
                viewModel.setLocation(mapView.mapCenter)
                viewModel.fetch(box.lonWest, box.latSouth, box.lonEast, box.latNorth)
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                viewModel.setZoomLevel(mapView.zoomLevelDouble)
                return false
            }
        }))

    }

    //TODO to vm and background task
    private fun setMarkers(list: List<ContainerAreaListModel>) {
        val markers = arrayListOf<Marker>()
        mapJob?.cancel()
        mapJob = lifecycleScope.launch(Dispatchers.IO) {
            Log.e("mapMarkers", "job start")
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
                Log.e("mapMarkers", "job end")
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