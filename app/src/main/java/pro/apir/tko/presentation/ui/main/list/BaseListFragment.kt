package pro.apir.tko.presentation.ui.main.list

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaListModel
import pro.apir.tko.presentation.extension.*
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 08.02.2020
 * Project: tko-android
 */
abstract class BaseListFragment : BaseFragment() {

    override fun layoutId() = R.layout.fragment_main_map_list

    abstract fun viewModel(): BaseListViewModel

    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    protected lateinit var bottomSheetLayout: ConstraintLayout
    protected lateinit var contentLayout: ConstraintLayout

    protected lateinit var layoutSearch: ConstraintLayout
    protected lateinit var etSearch: EditText
    protected lateinit var iconSearch: ImageView
    protected lateinit var progressSearch: ProgressBar

    protected lateinit var loadingList: ProgressBar
    protected lateinit var recyclerView: RecyclerView


    protected lateinit var textBottomHeader: TextView
    protected lateinit var btnAction: MaterialButton

    protected lateinit var btnMenu: ImageView
    protected lateinit var btnSearch: ImageView

    protected lateinit var mapView: MapView
    protected var mapJob: Job? = null

    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var markerOverlay: FolderOverlay? = null


    private val containersObserver = Observer<List<ContainerAreaListModel>> {
        Log.e("observer", "container base list")
        if (!it.isNullOrEmpty()) {
            setMarkers(it)
        }
    }

    protected fun setMap(mapView: MapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.zoomTo(12.0, 0L)

        val locationProvider = GpsMyLocationProvider(context)
        myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)
        myLocationOverlay?.setDirectionArrow(ContextCompat.getDrawable(context!!, R.drawable.ic_map_static)?.toBitmap(), ContextCompat.getDrawable(context!!, R.drawable.ic_map_arrow)?.toBitmap())
        if (viewModel().lastPosition == null) {
            myLocationOverlay?.enableFollowLocation()
        }
        mapView.overlayManager.add(myLocationOverlay)

        mapView.addMapListener(DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val box = mapView.boundingBox
                viewModel().setLocation(mapView.mapCenter)
                viewModel().fetchContainerAreas(box.lonWest, box.latSouth, box.lonEast, box.latNorth)
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                viewModel().setZoomLevel(mapView.zoomLevelDouble)
                return false
            }
        }))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetLayout = view.bottomsheetListMap
        contentLayout = view.contentMap

        layoutSearch = view.layoutSearch
        etSearch = view.etSearch
        iconSearch = view.iconSearch
        progressSearch = view.progressSearch

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

        btnSearch.setOnClickListener { viewModel().switchSearchMode() }

        btnMenu.setOnClickListener { globalState.toggleMenu() }

        setMap(mapView)

        bottomSheetLayout.addViewObserver {
            contentLayout.layoutParams.height = bottomSheetLayout.y.toInt() + 16.dpToPx
            contentLayout.requestLayout()
        }


        viewModel().containers.observe(viewLifecycleOwner, containersObserver)

        viewModel().searchContainersResults.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {

            } else {
                viewModel().containers.removeObserver(containersObserver)
                setMarkers(it, true)
            }
        })

        viewModel().searchLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                iconSearch.gone()
                progressSearch.visible()
            } else {
                progressSearch.gone()
                iconSearch.visible()
            }
        })


        viewModel().searchMode.observe(viewLifecycleOwner, Observer {
            layoutSearch.isInvisible = !it
            if (it) {
                etSearch.focusWithKeyboard()
            } else {
                viewModel().containers.observe(viewLifecycleOwner, containersObserver)
                hideKeyboard()
            }
        })

        viewModel().lastPosition?.let {
            mapView.controller.setCenter(it)
        }

        globalState.menuState.observe(viewLifecycleOwner, Observer {

            if (it == true) {
                btnMenu.gone()
            } else {
                btnMenu.visible()
            }

        })

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel().searchQuery(etSearch.getTextValue())
                hideKeyboard()
                true
            } else {
                false
            }
        }

        requireActivity()
                .onBackPressedDispatcher
                .addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (!layoutSearch.isVisible) {
                            findNavController().navigateUp()
                        } else {
                            viewModel().switchSearchMode()
                        }
                    }
                }
                )

    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()

        viewModel().zoomLevel?.let {
            mapView.controller.zoomTo(it, 0L)
        }

        viewModel().lastPosition?.let {
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


    //TODO to base vm and background task
    protected fun setMarkers(list: List<ContainerAreaListModel>, fromSearch: Boolean = false) {
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
                    marker.icon = if (fromSearch) {
                        ContextCompat.getDrawable(context!!, R.drawable.ic_map_marker_circle_orange)
                    } else {
                        ContextCompat.getDrawable(context!!, R.drawable.ic_map_marker_circle)
                    }
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    marker.position = location
                    marker.infoWindow = null
                    newFolderOverlay.add(marker)
                }
            }
            withContext(Dispatchers.Main) {
                mapView.overlayManager.remove(markerOverlay)
                mapView.overlayManager.add(newFolderOverlay)
                markerOverlay = newFolderOverlay
                Log.e("mapMarkers", "job end")
            }
        }

    }

}