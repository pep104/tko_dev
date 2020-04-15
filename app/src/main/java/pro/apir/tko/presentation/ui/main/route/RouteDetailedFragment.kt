package pro.apir.tko.presentation.ui.main.route

import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottomsheet_route_detailed.view.*
import kotlinx.android.synthetic.main.content_map_detailed.view.*
import kotlinx.android.synthetic.main.fragment_route_detailed.view.*
import kotlinx.android.synthetic.main.include_loading.view.*
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
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.presentation.extension.*
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */

class RouteDetailedFragment : BaseFragment(), RoutePointsAdapter.OnRoutePointClickedListener {


    private val viewModel: RouteDetailedViewModel by navGraphViewModels(R.id.graphRoute, { defaultViewModelProviderFactory })

    override fun layoutId() = R.layout.fragment_route_detailed

    override fun handleFailure() = viewModel.failure

    private lateinit var bottomSheetLayout: ConstraintLayout
    private lateinit var contentLayout: ConstraintLayout

    private lateinit var frameLoading: FrameLayout

    private lateinit var textHeader: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var stopAdapter: RoutePointsAdapter

    private lateinit var mapView: MapView
    private var mapJob: Job? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private lateinit var btnZoomIn: ImageButton
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnGeoSwitch: ImageButton

    private var pathOverlay: FolderOverlay? = null
    private var markersOverlay: FolderOverlay? = null

    private lateinit var btnStart: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteDetailedFragment(this)

        arguments?.let {
            val route: RouteModel? = it.getParcelable(KEY_ROUTE)
            if (route != null)
                viewModel.init(route)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetLayout = view.bottomsheetInventoryDetailed
        contentLayout = view.contentMapDetailed

        textHeader = view.textHeader

        frameLoading = view.frameLoading

        recyclerView = view.recyclerView
        stopAdapter = RoutePointsAdapter().apply { setListener(this@RouteDetailedFragment) }

        mapView = view.map
        btnZoomIn = view.btnZoomIn
        btnZoomOut = view.btnZoomOut
        btnGeoSwitch = view.btnGeoSwitch

        btnStart = view.btnStart

        with(recyclerView) {
            adapter = stopAdapter
            layoutManager = LinearLayoutManager(context)
        }

        btnGeoSwitch.setOnClickListener {
            viewModel.switchFollow()
        }

        btnStart.setOnClickListener {
            viewModel.startTracking()
        }

        view.btnBack.setOnClickListener(::back)

        setMap(mapView)

        observeViewModel()

        bottomSheetLayout.addViewObserver {
            contentLayout.layoutParams.height = bottomSheetLayout.y.toInt() + 16.dpToPx
            contentLayout.requestLayout()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.isFollowEnabled.value?.let {
            if (it) {
                myLocationOverlay?.enableMyLocation()
            }
        }
        mapView.onResume()

        viewModel.zoomLevel?.let {
            mapView.controller.zoomTo(it, 0L)
        }

        viewModel.lastPosition?.let {
            mapView.controller.setCenter(it)
        }
    }

    override fun onPause() {
        mapView.onPause()
        hideKeyboard()
        myLocationOverlay?.disableMyLocation()
        super.onPause()
    }

    private fun observeViewModel() {

        viewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RouteDetailedViewModel.RouteState.Default -> {
                    btnStart.text = getString(R.string.btn_route_start)
                    btnStart.visible()
                }
                is RouteDetailedViewModel.RouteState.Pending -> {
                    btnStart.text = getString(R.string.btn_route_continue)
                    btnStart.visible()
                }
                is RouteDetailedViewModel.RouteState.InProgress -> {
                    btnStart.gone()
                }
                else -> {
                    btnStart.gone()
                }
            }
        })


        viewModel.routeSession.observe(viewLifecycleOwner, Observer {
            textHeader.text = it.name

            setPath(it.path)

        })

        viewModel.routeStops.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                stopAdapter.setList(list)
                setMarkers(list)
            }

        })

        viewModel.loadingTrackingCompletion.observe(viewLifecycleOwner, Observer {
            frameLoading.isVisible = it
        })

        //controls etc
        viewModel.isFollowEnabled.observe(viewLifecycleOwner, Observer {
            it?.let { enabled ->

                if (enabled) {
                    btnGeoSwitch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blueMain), PorterDuff.Mode.SRC_IN)
                    myLocationOverlay?.enableFollowLocation()
                } else {
                    btnGeoSwitch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
                    myLocationOverlay?.disableFollowLocation()
                }

            }
        })

    }


    private fun setMap(mapView: MapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.zoomTo(17.0, 0L)

        val locationProvider = GpsMyLocationProvider(context)
        myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)
        myLocationOverlay?.setDirectionArrow(ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_static)?.toBitmap(), ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_arrow)?.toBitmap())


        mapView.overlayManager.add(myLocationOverlay)

        btnZoomIn.setOnClickListener {
            mapView.controller.zoomIn(200)
        }
        btnZoomOut.setOnClickListener {
            mapView.controller.zoomOut(150)
        }

        mapView.addMapListener(DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                Log.d("map", "follow: ${myLocationOverlay?.isFollowLocationEnabled}")
                if (myLocationOverlay?.isFollowLocationEnabled == false) {
                    viewModel.disableFollow()
                }
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                viewModel.setZoomLevel(mapView.zoomLevelDouble)
                return false
            }
        }))


    }


    override fun onRoutePointClicked(item: RoutePointModel, pos: Int) {
        viewModel.setStopPos(pos)
        findNavController().navigate(R.id.action_routeDetailedFragment_to_routeNavigationFragment)
    }

    //todo попробовать предзагрузку тайлов по маршруту?
    private fun setPath(list: List<CoordinatesModel>) {
        if (list.isNotEmpty()) {
            val firstItem = list.first()
            mapView.controller.setCenter(GeoPoint(firstItem.lat, firstItem.lng))

            val points = arrayListOf<GeoPoint>()
            list.forEach { points.add(GeoPoint(it.lat, it.lng)) }

            val polyline = Polyline()
            polyline.setPoints(points)
            polyline.outlinePaint.color = ContextCompat.getColor(requireContext(), R.color.bluePath)
            polyline.outlinePaint.isAntiAlias = true
            polyline.outlinePaint.strokeJoin = Paint.Join.ROUND
            polyline.outlinePaint.strokeCap = Paint.Cap.ROUND
            polyline.outlinePaint.strokeWidth = 13f

            val newFolderOverlay = FolderOverlay().apply { add(polyline) }

            mapView.overlayManager.remove(pathOverlay)
            mapView.overlayManager.add(newFolderOverlay)
            pathOverlay = newFolderOverlay
        }

    }

    //TODO to base vm
    private fun setMarkers(list: List<RoutePointModel>) {
        mapJob?.cancel()
        mapJob = lifecycleScope.launch(Dispatchers.IO) {
            val newFolderOverlay = FolderOverlay()
            list.forEach {

                val coordinates = it.coordinates
                if (coordinates != null
                        && coordinates.lat != 0.0 && coordinates.lng != 0.0
                        && coordinates.lat in -85.05..85.05) {
                    val location = GeoPoint(coordinates.lat, coordinates.lng)
                    val marker = Marker(mapView)

                    when (it.type) {
                        RouteStateConstants.POINT_TYPE_PENDING -> {
                            marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_pending)
                            marker.setAnchor(Marker.ANCHOR_CENTER, 0.88f)
                        }
                        RouteStateConstants.POINT_TYPE_COMPLETED -> {
                            marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_marker_circle_completed)
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        }
                        RouteStateConstants.POINT_TYPE_DEFAULT -> {
                            marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_marker_circle)
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        }
                    }

                    marker.position = location
                    marker.infoWindow = null
                    newFolderOverlay.add(marker)

                }
            }
            withContext(Dispatchers.Main) {
                mapView.overlayManager.remove(markersOverlay)
                mapView.overlayManager.add(newFolderOverlay)
                markersOverlay = newFolderOverlay
            }
        }

    }

    companion object {

        const val KEY_ROUTE = "key_route"

    }

}