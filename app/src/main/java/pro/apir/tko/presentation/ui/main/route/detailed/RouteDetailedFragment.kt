package pro.apir.tko.presentation.ui.main.route.detailed

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottomsheet_route_detailed.view.*
import kotlinx.android.synthetic.main.content_map_detailed.view.*
import kotlinx.android.synthetic.main.fragment_route_detailed.view.*
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
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.entities.RouteStop
import pro.apir.tko.presentation.extension.*
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
//TODO EXTRACT CONTROLS etc TO BASE DETAILED FRAGMENT
class RouteDetailedFragment : BaseFragment(), RouteStopPointsAdapter.OnRoutePointClickedListener {

    private val viewModel: RouteDetailedViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_route_detailed

    override fun handleFailure() = viewModel.failure

    private lateinit var bottomSheetLayout: ConstraintLayout
    private lateinit var contentLayout: ConstraintLayout

    private lateinit var textHeader: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var stopAdapter: RouteStopPointsAdapter

    private lateinit var mapView: MapView
    private var mapJob: Job? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private lateinit var btnZoomIn: ImageButton
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnGeoSwitch: ImageButton

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

        recyclerView = view.recyclerView
        stopAdapter = RouteStopPointsAdapter().apply { setListener(this@RouteDetailedFragment) }

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

        view.btnBack.setOnClickListener(::back)

        setMap(mapView)

        observeViewModel()

        bottomSheetLayout.addViewObserver {
            contentLayout.layoutParams.height = bottomSheetLayout.y.toInt() + 16.dpToPx
            contentLayout.requestLayout()
        }
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


        viewModel.route.observe(viewLifecycleOwner, Observer {
            textHeader.text = it.name

            setPath(it.path)

        })

        viewModel.routeStops.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                stopAdapter.setList(list)
                setMarkers(list)
            }

        })


        //controls etc
        viewModel.isFollowEnabled.observe(viewLifecycleOwner, Observer {
            it?.let { enabled ->

                if (enabled) {
                    btnGeoSwitch.setColorFilter(ContextCompat.getColor(context!!, R.color.blueMain), PorterDuff.Mode.SRC_IN)
                    myLocationOverlay?.enableFollowLocation()
                } else {
                    btnGeoSwitch.setColorFilter(ContextCompat.getColor(context!!, R.color.black), PorterDuff.Mode.SRC_IN)
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
        myLocationOverlay?.setDirectionArrow(ContextCompat.getDrawable(context!!, R.drawable.ic_map_static)?.toBitmap(), ContextCompat.getDrawable(context!!, R.drawable.ic_map_arrow)?.toBitmap())


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


    override fun onRoutePointClicked(item: RouteStop, pos: Int) {
        //TODO
        toast("clicked: ${item.stop.location}")
    }

    //TEMP fixme ???
    //todo попробовать предзагрузку тайлов по маршруту?
    private fun setPath(list: List<CoordinatesModel>) {
        if (list.isNotEmpty()) {
            val firstItem = list.first()
            mapView.setExpectedCenter(GeoPoint(firstItem.lat, firstItem.lng))

            val points = arrayListOf<GeoPoint>()
            list.forEach { points.add(GeoPoint(it.lat, it.lng)) }

            val polyline = Polyline()
            polyline.outlinePaint.color = Color.parseColor("#3469A8")
            polyline.outlinePaint.alpha = 196
            polyline.setPoints(points)


            mapView.overlayManager.add(polyline)

        }

    }

    //TODO to base vm and background task
    private fun setMarkers(list: List<RouteStop>) {
        val markers = arrayListOf<Marker>()
        mapJob?.cancel()
        mapJob = lifecycleScope.launch(Dispatchers.IO) {
            Log.e("mapMarkers", "job start")
            list.forEach {
                val coordinates = it.stop.coordinates
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
                mapView.overlayManager.addAll(markers)
                Log.e("mapMarkers", "job end")
            }
        }

    }

    private fun setPoints(list: List<RouteStop>) {

    }

    companion object {

        const val KEY_ROUTE = "key_route"

    }

}