package pro.apir.tko.presentation.ui.main.list

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
import kotlinx.android.synthetic.main.bottomsheet_list_map.view.*
import kotlinx.android.synthetic.main.content_map.view.*
import kotlinx.android.synthetic.main.fragment_main_map_list.view.*
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
import pro.apir.tko.presentation.ui.main.list.container.ContainerListAdapter

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class MainListMapFragment : BaseFragment(), ContainerListAdapter.OnItemClickListener {

    private val viewModel: MainListMapViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_main_map_list

    override fun handleFailure() = viewModel.failure

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var bottomSheetLayout: ConstraintLayout
    private lateinit var contentLayout: ConstraintLayout

    private lateinit var layoutSearch: ConstraintLayout
    private lateinit var etSearch: EditText

    private lateinit var loadingList: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var containerListAdapter: ContainerListAdapter

    private lateinit var textBottomHeader: TextView
    private lateinit var btnAction: MaterialButton

    private lateinit var btnMenu: ImageView
    private lateinit var btnSearch: ImageView

    private lateinit var mapView: MapView
    private var mapJob: Job? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private val containersListObserver = Observer<List<ContainerAreaListModel>> {
        it?.let { list ->
            containerListAdapter.setList(list)
            loadingList.goneWithFade()
        }
    }

    private val routesListObserver = Observer<List<Any>> {
        it?.let { list ->
            loadingList.goneWithFade()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryListFragment(this)
        arguments?.let { viewModel.init(it.getString(KEY_TYPE, TYPE_INVENTORY)) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetLayout = view.bottomsheetListMap
        contentLayout = view.contentMap

        layoutSearch = view.layoutSearch
        etSearch = view.etSearch

        recyclerView = view.recyclerView
        loadingList = view.loadingList

        textBottomHeader = view.textHeader

        btnAction = view.btnAdd
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

        containerListAdapter = ContainerListAdapter().apply { setListener(this@MainListMapFragment) }
        recyclerView.adapter = containerListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)


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
        containerListAdapter.setListener(null)
        super.onDestroyView()
    }

    private fun observeViewModel() {

        viewModel.type.observe(viewLifecycleOwner, Observer {
            when (it) {
                TYPE_INVENTORY -> {
                    setInventoryType()
                }
                TYPE_ROUTE -> {
                    setRouteType()
                }
            }

        })

        viewModel.containers.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                setMarkers(it)
            }
        })

        viewModel.searchMode.observe(viewLifecycleOwner, Observer {
            layoutSearch.isInvisible = !it
            if (it) {
                etSearch.focusWithKeyboard()
            } else {
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
        myLocationOverlay?.setDirectionArrow(ContextCompat.getDrawable(context!!, R.drawable.ic_map_static)?.toBitmap(), ContextCompat.getDrawable(context!!, R.drawable.ic_map_arrow)?.toBitmap())
        if (viewModel.lastPosition == null) {
            myLocationOverlay?.enableFollowLocation()
        }
        mapView.overlayManager.add(myLocationOverlay)

        mapView.addMapListener(DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val box = mapView.boundingBox
                viewModel.setLocation(mapView.mapCenter)
                viewModel.fetchContainerAreas(box.lonWest, box.latSouth, box.lonEast, box.latNorth)
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                viewModel.setZoomLevel(mapView.zoomLevelDouble)
                return false
            }
        }))

    }

    fun setInventoryType() {
        textBottomHeader.text = getString(R.string.text_inventory_list_header)
        btnAction.text = getString(R.string.btn_add_container)
        btnAction.setOnClickListener { findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryEditFragment) }
        viewModel.containers.observe(viewLifecycleOwner, containersListObserver)
    }

    fun setRouteType() {
        textBottomHeader.text = getString(R.string.text_route_list_header)
        btnAction.text = getString(R.string.btn_choose_route)
        btnAction.setOnClickListener {
            //todo to route
        }
        viewModel.containers.observe(viewLifecycleOwner, routesListObserver)
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

    override fun onContainerItemClicked(item: ContainerAreaListModel) {
        findNavController().navigate(R.id.action_inventoryListFragment_to_inventoryDetailedFragment,
                bundleOf(
                        InventoryDetailedFragment.KEY_ID to item.id,
                        InventoryDetailedFragment.KEY_HEADER to item.location,
                        InventoryDetailedFragment.KEY_COORDINATES to item.coordinates
                ))
    }

    companion object {

        const val KEY_TYPE = "type"
        const val TYPE_INVENTORY = "inventory"
        const val TYPE_ROUTE = "route"

    }

}