package pro.apir.tko.presentation.ui.main.inventory.detailed

import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottomsheet_inventory_detailed.view.*
import kotlinx.android.synthetic.main.content_map_detailed.view.*
import kotlinx.android.synthetic.main.fragment_inventory_detailed.view.*
import kotlinx.coroutines.Job
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
import pro.apir.tko.domain.model.CoordinatesModel
import pro.apir.tko.presentation.extension.addViewObserver
import pro.apir.tko.presentation.extension.dpToPx
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.extension.visible
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.inventory.edit.InventoryEditFragment
import pro.apir.tko.presentation.ui.main.inventory.edit.InventoryEditSharedViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-19
 * Project: tko-android
 */
//TODO EXTRACT CONTROLS etc TO BASE DETAILED FRAGMENT
class InventoryDetailedFragment : BaseFragment() {

    private val viewModel: InventoryDetailedViewModel by viewModels()
    private val sharedEditViewModel: InventoryEditSharedViewModel by activityViewModels()

    override fun layoutId() = R.layout.fragment_inventory_detailed

    override fun handleFailure() = viewModel.failure

    private lateinit var loading: ProgressBar

    private lateinit var bottomSheetLayout: ConstraintLayout
    private lateinit var contentLayout: ConstraintLayout

    private lateinit var textHeader: TextView
    private lateinit var textContainerInfo: TextView
    private lateinit var textContainerNumber: TextView

    private lateinit var imgClose: ImageView
    private lateinit var imgThrash: ImageView

    private lateinit var btnBack: ImageView
    private lateinit var btnSearch: ImageView
    private lateinit var btnEdit: MaterialButton

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var adapter: ContainerImagesAdapter

    private lateinit var mapView: MapView
    private var mapJob: Job? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private lateinit var btnZoomIn: ImageButton
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnGeoSwitch: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryDetailedFragment(this)

        arguments?.let {
            val id = it.getLong(KEY_ID, 0L)
            val header = it.getString(KEY_HEADER, "")
            val coordinates = it.getParcelable<CoordinatesModel>(KEY_COORDINATES)
            viewModel.fetchInfo(id, header, coordinates)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading = view.loading

        bottomSheetLayout = view.bottomsheetInventoryDetailed
        contentLayout = view.contentMapDetailed

        textHeader = view.textHeader
        textContainerInfo = view.textContainerInfo
        textContainerNumber = view.textContainerNumber

        imgThrash = view.imgThrash
        imgClose = view.imgClose
        btnBack = view.btnBack
        btnEdit = view.btnEdit

        mapView = view.map
        btnZoomIn = view.btnZoomIn
        btnZoomOut = view.btnZoomOut
        btnGeoSwitch = view.btnGeoSwitch

        imageRecyclerView = view.imageRecyclerView
        adapter = ContainerImagesAdapter()
        imageRecyclerView.adapter = adapter

        imgClose.setOnClickListener(::back)
        btnBack.setOnClickListener(::back)
        btnEdit.setOnClickListener { findNavController().navigate(R.id.action_inventoryDetailedFragment_to_inventoryEditFragment, bundleOf(InventoryEditFragment.KEY_CONTAINER to viewModel.data.value)) }

        btnGeoSwitch.setOnClickListener {
            viewModel.switchFollow()
        }

        setMap(mapView)

        observeViewModel()

        bottomSheetLayout.addViewObserver {
            contentLayout.layoutParams.height = bottomSheetLayout.y.toInt() + 16.dpToPx
            contentLayout.requestLayout()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()
    }

    override fun onPause() {
        mapView.onPause()
        myLocationOverlay?.disableMyLocation()
        super.onPause()
    }

    private fun observeViewModel() {

        viewModel.data.observe(viewLifecycleOwner, Observer {
            it?.let {
                btnEdit.isEnabled = true

                val pluredCount = resources.getQuantityString(
                        R.plurals.plurals_containers,
                        it.containersCount
                                ?: 0, it.containersCount)
                val area = it.area ?: 0.0

                textContainerInfo.text = getString(R.string.text_container_detailed_info, pluredCount, area.toString())

                imageRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                it.photos?.let { photos -> adapter.setData(photos) }

                loading.goneWithFade()
                imgThrash.visible()
            }
        })

        viewModel.header.observe(viewLifecycleOwner, Observer {
            textHeader.text = it
        })

        viewModel.coordinates.observe(viewLifecycleOwner, Observer {
            it?.let {
                setMapPoint(it.lat, it.lng)
            }
        })

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

        sharedEditViewModel.containerArea.observe(viewLifecycleOwner, Observer {
            it?.let { container ->
                viewModel.setEditedData(container)
                sharedEditViewModel.consume()
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

                return false
            }
        }))


    }

    private fun setMapPoint(lat: Double, lng: Double) {
        mapView.overlays.clear()
        mapView.controller.setCenter(GeoPoint(lat, lng))
        val location = GeoPoint(lat, lng)
        val marker = Marker(mapView)
        marker.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_map_pin)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.position = location
        mapView.overlays.add(marker)
        mapView.overlayManager.add(myLocationOverlay)
    }

    companion object {

        const val KEY_ID = "id"
        const val KEY_HEADER = "header"
        const val KEY_COORDINATES = "coordinates"

    }

}