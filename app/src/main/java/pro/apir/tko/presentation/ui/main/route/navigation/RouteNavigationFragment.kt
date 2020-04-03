package pro.apir.tko.presentation.ui.main.route.navigation

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_route_navigation.view.*
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
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.extension.round
import pro.apir.tko.domain.failure.RouteTrackingNotCompleted
import pro.apir.tko.domain.failure.RouteTrackingNotExist
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.domain.model.PhotoModel
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.presentation.extension.*
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.camera.CameraSharedViewModel2
import pro.apir.tko.presentation.ui.main.route.RouteDetailedViewModel
import ru.sarmatin.mobble.utils.consumablelivedata.ConsumableObserver

/**
 * Created by Антон Сарматин
 * Date: 10.02.2020
 * Project: tko-android
 *
 */
class RouteNavigationFragment : BaseFragment(), RoutePointPhotoAttachAdapter.AttachInteractionListener {


    private val viewModel: RouteDetailedViewModel by navGraphViewModels(R.id.graphRoute) { defaultViewModelProviderFactory }
    private val cameraSharedViewModel2: CameraSharedViewModel2 by activityViewModels()

    override fun layoutId() = R.layout.fragment_route_navigation

    override fun handleFailure() = viewModel.failure

    override val failureObserver: Observer<Failure> = Observer {
        if (it is Failure.FeatureFailure) {
            when (it) {
                is RouteTrackingNotExist -> {
                    alert(R.string.error_route_tracking_not_exist)
                }
                is RouteTrackingNotCompleted -> {
                    alert(R.string.error_route_not_completed)
                }
            }
        } else super.failureObserver
    }

    private lateinit var btnAction: MaterialButton
    private lateinit var btnFinish: MaterialButton

    private lateinit var cardPoint: CardView
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var dividerPrevious: View
    private lateinit var dividerNext: View
    private lateinit var textLocationHeader: TextView
    private lateinit var imgThrash: ImageView
    private lateinit var textContainerInfo: TextView
    private lateinit var textContainerNumber: TextView
    private lateinit var imgLocation: ImageView
    private lateinit var textDistance: TextView

    //etc

    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var recyclerViewAdapter: RoutePointPhotoAttachAdapter

    private lateinit var frameLoading: FrameLayout


    private lateinit var mapView: MapView
    private var mapJob: Job? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private var pathOverlay: FolderOverlay? = null
    private var markersOverlay: FolderOverlay? = null

    private lateinit var btnZoomIn: ImageButton
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnGeoSwitch: ImageButton

    private var currentState: Int? = -123

    //DIFFS
    private var lastPoint: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectRouteNavigationFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardPoint = view.cardPoint
        btnNext = view.btnNext
        btnPrevious = view.btnPrevious
        dividerNext = view.dividerNext
        dividerPrevious = view.dividerPrevious
        textLocationHeader = view.textLocationHeader
        imgThrash = view.imgThrash
        textContainerInfo = view.textContainerInfo
        textContainerNumber = view.textContainerNumber
        imgLocation = view.imgLocation
        textDistance = view.textDistance

        frameLoading = view.frameLoading

        mapView = view.map
        btnZoomIn = view.btnZoomIn
        btnZoomOut = view.btnZoomOut
        btnGeoSwitch = view.btnGeoSwitch

        recyclerViewPhotos = view.recyclerViewPhotos
        recyclerViewAdapter = RoutePointPhotoAttachAdapter().apply { setListener(this@RouteNavigationFragment) }
        with(recyclerViewPhotos) {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        btnAction = view.btnAction
        btnFinish = view.btnFinish

        btnGeoSwitch.setOnClickListener {
            viewModel.switchFollow()
        }

        setMap(mapView)

        observeViewModel()

        btnNext.setOnClickListener {
            viewModel.nextStop()
        }

        btnPrevious.setOnClickListener {
            viewModel.previousStop()
        }

        view.btnBack.setOnClickListener { findNavController().navigateUp() }
        view.btnSearch.invisible()

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

    override fun onDestroyView() {
        recyclerViewAdapter.setListener(null)
        super.onDestroyView()
    }

    private fun observeViewModel() {

        viewModel.currentStop.observe(viewLifecycleOwner, Observer {
            it?.let {
                setCardviewData(it)
                setCardviewState(it)
            }
        })

        viewModel.currentStopStickyCoordinates.observe(viewLifecycleOwner, Observer {
            it?.let {
                mapView.controller.setCenter(GeoPoint(it.lat, it.lng))
            }
        })

        viewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                RouteDetailedViewModel.RouteState.Default,
                RouteDetailedViewModel.RouteState.Pending,
                RouteDetailedViewModel.RouteState.Disabled -> {
                    btnAction.gone()
                    btnFinish.gone()
                }
                RouteDetailedViewModel.RouteState.InProgress -> {
                    btnAction.visible()
                    btnFinish.gone()
                }

                RouteDetailedViewModel.RouteState.Completed -> {
                    btnAction.gone()
                    btnFinish.visible()
                    btnFinish.setOnClickListener {
                        viewModel.finishTracking()
                    }
                }
            }
        })

        viewModel.loadingStopCompletion.observe(viewLifecycleOwner, Observer {
            frameLoading.isVisible = it
        })

        viewModel.loadingTrackingCompletion.observe(viewLifecycleOwner, Observer {
            frameLoading.isVisible = it
        })

        viewModel.eventTrackingCompletion.observe(viewLifecycleOwner, Observer {
            if (it)
                findNavController().popBackStack(R.id.routeListFragment, false)
            else
                alert(getString(R.string.error_default))
        })

        //Route

        viewModel.routeSession.observe(viewLifecycleOwner, Observer {
            setPath(it.path)
        })

        viewModel.routeStops.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                setMarkers(list)
            }
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

        //shared

        cameraSharedViewModel2.photoPathList.observe(viewLifecycleOwner, ConsumableObserver {
            viewModel.addPhotos(it)
        })

    }

    private fun setCardviewData(point: RoutePointModel) {
        textLocationHeader.text = point.location

        val pluredCount = resources.getQuantityString(
                R.plurals.plurals_containers,
                point.containersCount
                        ?: 0, point.containersCount)
        val area = point.containersVolume ?: 0.0

        textContainerInfo.text = getString(R.string.text_container_detailed_info, pluredCount, area.toString())
        textContainerNumber.text = point.registryNumber

        val distance = point.distance
        textDistance.text = when (true) {
            distance != null && distance < 1000 -> {
                textDistance.context.getString(R.string.text_route_point_distance_meters, distance.toString())
            }
            distance != null && distance > 1000 -> {
                val kms = (distance.toDouble() / 1000).round(2)
                textDistance.context.getString(R.string.text_route_point_distance_kilometers, kms.toString())
            }
            else -> {
                ""
            }
        }
    }

    private fun setCardviewState(point: RoutePointModel) {
        Log.d("point", "setPointState: ${point.location} ${point.type}")
        val isNewPoint = if (lastPoint != point.pointId) {
            lastPoint = point.pointId
            true
        } else {
            false
        }

        val photosCount = point.photos.size

        when (point.type) {
            RouteStateConstants.POINT_TYPE_DEFAULT -> {
                btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_default)
                btnAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.blueMain))

                cardPoint.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                textLocationHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPointHeaderGrey))
                textContainerInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                textContainerNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                textDistance.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                dividerPrevious.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.defaultRouteDivider))
                dividerNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.defaultRouteDivider))

                imgLocation.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_geolocation))
                imgThrash.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash))

                btnNext.apply {
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right_blue))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.ripple_nav_icon_blue)
                }
                btnPrevious.apply {
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_left_blue))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.ripple_nav_icon_blue)
                }

            }
            RouteStateConstants.POINT_TYPE_PENDING -> {

                if (photosCount > 1) {
                    btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_completed)
                } else {
                    btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_pending)
                }

                btnAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                cardPoint.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blueMain))

                textLocationHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                textContainerInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                textContainerNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                textDistance.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                dividerPrevious.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.stateRouteDivider))
                dividerNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.stateRouteDivider))

                imgLocation.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_geolocation_white))
                imgThrash.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash_white))

                btnNext.apply {
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.ripple_nav_icon_light)
                }
                btnPrevious.apply {
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_left))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.ripple_nav_icon_light)
                }

            }
            RouteStateConstants.POINT_TYPE_COMPLETED -> {
                btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_completed)
                btnAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                cardPoint.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))

                textLocationHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                textContainerInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                textContainerNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                textDistance.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                dividerPrevious.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.stateRouteDivider))
                dividerNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.stateRouteDivider))

                imgLocation.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_geolocation_white))
                imgThrash.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash_white))

                btnNext.apply {
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.ripple_nav_icon_light)
                }
                btnPrevious.apply {
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_left))
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.ripple_nav_icon_light)
                }


            }

        }

        setRecycler(point, isNewPoint)


    }

    private fun setRecycler(point: RoutePointModel, isNewPoint: Boolean) {
        val photosCount = point.photos.size

        recyclerViewAdapter.setPhoto(point.photos, point.type)

        when (point.type) {
            RouteStateConstants.POINT_TYPE_DEFAULT,
            RouteStateConstants.POINT_TYPE_PENDING -> {
                if (photosCount > 0) {
                    enablePhotoRecycler()
                } else {
                    if (isNewPoint) {
                        disablePhotoRecycler()
                    }
                }
            }
            RouteStateConstants.POINT_TYPE_COMPLETED -> {
                recyclerViewPhotos.visible()
                btnAction.text = getString(R.string.btn_route_action_next)
                btnAction.setOnClickListener {
                    viewModel.nextStop()
                }
            }
        }
    }

    private fun enablePhotoRecycler() {
        recyclerViewPhotos.visible()
        btnAction.text = getString(R.string.btn_route_action_done)
        btnAction.setOnClickListener {
            viewModel.completePoint()
        }
    }

    private fun disablePhotoRecycler() {
        recyclerViewPhotos.gone()
        btnAction.text = getString(R.string.btn_route_action_add_photos)
        btnAction.setOnClickListener {
            enablePhotoRecycler()
        }
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

            val newFolderOverlay = FolderOverlay().apply { add(polyline) }

            mapView.overlayManager.remove(pathOverlay)
            mapView.overlayManager.add(newFolderOverlay)
            pathOverlay = newFolderOverlay
        }

    }

    private fun setMarkers(list: List<RoutePointModel>) {

        mapJob?.cancel()
        mapJob = lifecycleScope.launch(Dispatchers.IO) {
            Log.e("mapMarkers", "job start")
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
                    newFolderOverlay.add(marker)
                }
            }
            withContext(Dispatchers.Main) {

                mapView.overlayManager.remove(markersOverlay)
                mapView.overlayManager.add(newFolderOverlay)
                markersOverlay = newFolderOverlay
                Log.e("mapMarkers", "job end")
            }
        }

    }

    override fun onDeletePhoto(photo: PhotoModel) {
        viewModel.deletePhoto(photo)
    }

    override fun onAddNewPhoto() {
        findNavController().navigate(R.id.action_routeNavigationFragment_to_cameraFragment2)
    }


}