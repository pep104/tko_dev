package pro.apir.tko.presentation.ui.main.address

import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import com.redmadrobot.inputmask.model.Notation
import kotlinx.android.synthetic.main.fragment_address.view.*
import kotlinx.android.synthetic.main.fragment_address.view.btnBack
import kotlinx.android.synthetic.main.fragment_address.view.btnSearch
import kotlinx.android.synthetic.main.fragment_address.view.map
import kotlinx.android.synthetic.main.fragment_route_navigation.view.*
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
import pro.apir.tko.core.constant.extension.round
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.presentation.extension.*
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
//Fragment for pick or edit address of container area
class AddressFragment : BaseFragment(), AddressSearchAdapter.OnItemClickListener {

    private val viewModel: AddressViewModel by viewModels()
    private val sharedViewModel: AddressSharedViewModel by activityViewModels()

    override fun layoutId() = R.layout.fragment_address

    override fun handleFailure() = viewModel.failure

    private lateinit var textTitle: TextView

    private lateinit var cardBottom: CardView
    private lateinit var cardSearch: CardView
    private lateinit var cardCoordinates: CardView

    private lateinit var textAddress: TextView
    private lateinit var textCoordinates: TextView

    private lateinit var etAddress: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: AddressSearchAdapter

    private lateinit var btnSave: MaterialButton
    private lateinit var btnEditCoordinates: ImageView
    private lateinit var btnCopy: ImageView
    private lateinit var btnClearAddress: ImageView

    private lateinit var textAddressCoordinates: TextView
    private lateinit var etCoordinates: EditText
    private lateinit var btnClearCoordinates: ImageView
    private lateinit var dividerCoordinatesEt: View
    private lateinit var textErrorCoordinates: TextView
    private lateinit var btnSubmitCoordinates: MaterialButton

    private val mapListener: MapListener = object : MapListener {
        override fun onScroll(event: ScrollEvent?): Boolean {
            val lat = mapView.mapCenter.latitude.round(6)
            val lon = mapView.mapCenter.longitude.round(6)
            Log.e("map", "$lat $lon")
            viewModel.updateCoordinatesOnDragEvent(lat, lon)
            setMapPoint(lat, lon, false)
            removeCoordinatesMaskedTextListener()
            etCoordinates.setText(
                getString(
                    R.string.text_coordinates_placeholder,
                    lat.toString(),
                    lon.toString()
                )
            )
            setCoordinatesMaskedTextListener()
            return false
        }

        override fun onZoom(event: ZoomEvent?): Boolean {
            return false
        }
    }

    val maskedListener: MaskedTextChangedListener by lazy {
        MaskedTextChangedListener(
            field = etCoordinates,
            primaryFormat = getString(R.string.mask_coordinates),
            affineFormats = emptyList(),
            customNotations = listOf(Notation('m', "-", true)),
            affinityCalculationStrategy = AffinityCalculationStrategy.WHOLE_STRING,
            autocomplete = true,
            autoskip = false,
            listener = null,
            valueListener = object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {
                    viewModel.processInput(extractedValue)
                }
            })
    }


    private lateinit var mapView: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private var addressPointOverlay: FolderOverlay? = null

    private lateinit var btnZoomIn: ImageButton
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnGeoSwitch: ImageButton

    private val addressWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel.query(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectAddressFragment(this)

        //Try to get AddressModel parcelable
        arguments?.let { bundle ->
            if (bundle.containsKey(KEY_ADDRESS)) {
                viewModel.selectAddress(
                    addressModel = bundle.get(KEY_ADDRESS) as AddressModel,
                    forceQuery = true
                )
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textTitle = view.title

        cardBottom = view.cardBottom
        cardSearch = view.cardSearch
        cardCoordinates = view.cardCoordinates

        //Search card
        etAddress = view.etAddress
        btnClearAddress = view.btnClearAddress
        recyclerView = view.recyclerView
        suggestionAdapter = AddressSearchAdapter()
        suggestionAdapter.setListener(this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = suggestionAdapter

        //Bottom card
        textAddress = view.textAddress
        textCoordinates = view.textCoordinates
        btnEditCoordinates = view.btnEdit
        btnCopy = view.btnCopy

        //Coordinates card
        textAddressCoordinates = view.textCoordinatesAddressCard
        etCoordinates = view.etCoordinatesCard
        btnClearCoordinates = view.btnClearCoordinates
        dividerCoordinatesEt = view.dividerCoordinatesEt
        textErrorCoordinates = view.textErrorCoordinates
        btnSubmitCoordinates = view.btnSubmitCoordinates

        btnSave = view.btnSave


        mapView = view.map
        btnZoomIn = view.btnZoomIn
        btnZoomOut = view.btnZoomOut
        btnGeoSwitch = view.btnGeoSwitch

        setMap(mapView)


        btnClearAddress.setOnClickListener {
            etAddress.clearInput()
        }

        btnEditCoordinates.setOnClickListener {
            viewModel.setViewType(AddressViewModel.ViewType.LOCATION_COORDINATES)
        }

        btnClearCoordinates.setOnClickListener {
            etCoordinates.clearInput()
        }

        btnSubmitCoordinates.setOnClickListener {
            viewModel.setViewType(AddressViewModel.ViewType.BOTTOM_CARD)
        }

        btnGeoSwitch.setOnClickListener {
            viewModel.switchFollow()
        }

        view.btnSearch.setOnClickListener {
            viewModel.setViewType(AddressViewModel.ViewType.SEARCH)
        }

        view.viewAddressOverlay.setOnClickListener {
            viewModel.setViewType(AddressViewModel.ViewType.SEARCH)
        }

        view.btnSave.setOnClickListener {
            //Set current address to shared VM and pop back
            viewModel.address.value?.let { sharedViewModel.setAddress(it) }
            findNavController().popBackStack()
        }

        view.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btnCopy.setOnClickListener {
            copyToClipboard(etAddress.getTextValue())
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //Close this fragment only if state is 0 (bottom card visible)
                    if (cardBottom.isVisible()) {
                        findNavController().navigateUp()
                    } else {
                        //Else show bottom card
                        viewModel.setViewType(AddressViewModel.ViewType.BOTTOM_CARD)
                    }
                }
            }
            )

        observeViewModel()
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

        //TODO
//        viewModel.lastPosition?.let {
//            mapView.controller.setCenter(it)
//        }
    }

    override fun onPause() {
        mapView.onPause()
        myLocationOverlay?.disableMyLocation()
        super.onPause()
    }

    private fun observeViewModel() {
        viewModel.suggestions.observe(viewLifecycleOwner, Observer {
            suggestionAdapter.setList(it)
        })

        viewModel.address.observe(viewLifecycleOwner, Observer {
            textAddress.isEnabled = true
            textAddress.text = it.value
            etAddress.applyUnwatchable(addressWatcher) {
                setText(it.value)
                setSelection(it.value.length)
            }

            textAddressCoordinates.isEnabled = true
            textAddressCoordinates.text = it.value

            if (it.lat != null && it.lng != null) {
                textCoordinates.isEnabled = true
                val coordinatesText = getString(
                    R.string.text_coordinates_placeholder,
                    it.lat.toString(),
                    it.lng.toString()
                )
                textCoordinates.text = coordinatesText
                myLocationOverlay?.disableFollowLocation()
                btnCopy.visible()
                setMapPoint(it.lat!!, it.lng!!)
            } else {
                textCoordinates.isEnabled = false
                textCoordinates.text = getString(R.string.text_coordinates_not_found)
                etCoordinates.setText("")
            }
        })

        viewModel.viewType.observe(viewLifecycleOwner, Observer {
            removeCoordinatesMaskedTextListener()
            mapView.removeMapListener(mapListener)
            textTitle.text = getString(R.string.text_address_title)
            when (it) {
                AddressViewModel.ViewType.BOTTOM_CARD -> {
                    etAddress.removeTextChangedListener(addressWatcher)
                    hideKeyboard()
                    cardSearch.gone()
                    cardCoordinates.gone()
                    btnSave.visible()
                    cardBottom.visible()
                }
                AddressViewModel.ViewType.SEARCH -> {
                    etAddress.addTextChangedListener(addressWatcher)

                    viewModel.address.value?.let { addressModel ->
                        etAddress.setText(addressModel.value)
                    }

                    cardBottom.invisible()
                    btnSave.invisible()
                    cardSearch.visible()
                    etAddress.focusWithKeyboard()
                    etAddress.placeCursorToEnd()

                }
                AddressViewModel.ViewType.LOCATION_COORDINATES -> {
                    textTitle.text = getString(R.string.text_address_title_coordinates)
                    mapView.addMapListener(mapListener)
                    btnSave.invisible()
                    cardBottom.invisible()
                    cardCoordinates.visible()
                    //FIXME user location for null coordinates
                    etCoordinates.setText(textCoordinates.text)
                    etCoordinates.hint = textCoordinates.text
                    setCoordinatesMaskedTextListener()
                }
            }
        })

        //coordinates edit
        viewModel.errorCoordinates.observe(viewLifecycleOwner, Observer { isError ->
            textErrorCoordinates.isVisible = isError
        })


        //controls etc
        viewModel.isFollowEnabled.observe(viewLifecycleOwner, Observer { isFollowEnabled ->
            isFollowEnabled?.let { enabled ->

                if (enabled) {
                    btnGeoSwitch.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blueMain
                        ), PorterDuff.Mode.SRC_IN
                    )
                    myLocationOverlay?.enableFollowLocation()
                } else {
                    btnGeoSwitch.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        ), PorterDuff.Mode.SRC_IN
                    )
                    myLocationOverlay?.disableFollowLocation()

                    val point = viewModel.address.value
                    point?.let {
                        if (it.lat != null && it.lng != null)
                            setMapPoint(it.lat!!, it.lng!!)
                    }

                }

            }
        })

    }


    //Configure map view
    private fun setMap(mapView: MapView) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.zoomTo(12.0, 0L)

        val locationProvider = GpsMyLocationProvider(context)
        myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)
        myLocationOverlay?.setCustomLocationMarkers()

        if (viewModel.address.value == null) {
            myLocationOverlay?.enableFollowLocation()
            viewModel.enableFollow()
        }

        mapView.overlayManager.add(myLocationOverlay)

        btnZoomIn.setOnClickListener {
            mapView.controller.zoomIn(200)
        }
        btnZoomOut.setOnClickListener {
            mapView.controller.zoomOut(150)
        }

        mapView.addMapListener(DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
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

    //Set current map point from coordinates
    private fun setMapPoint(lat: Double, lng: Double, move: Boolean = true) {
        if (move)
            mapView.controller.animateTo(GeoPoint(lat, lng))

        val location = GeoPoint(lat, lng)
        val marker = Marker(mapView)
        marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin)
        marker.setAnchor(Marker.ANCHOR_CENTER, 0.88f)
        marker.position = location
        marker.infoWindow = null

        val newOverlay = FolderOverlay().apply { add(marker) }

        mapView.overlayManager.remove(addressPointOverlay)
        mapView.overlayManager.add(newOverlay)
        addressPointOverlay = newOverlay

    }

    private fun setCoordinatesMaskedTextListener() {
        etCoordinates.addTextChangedListener(maskedListener)
        etCoordinates.onFocusChangeListener = maskedListener
    }

    private fun removeCoordinatesMaskedTextListener() {
        etCoordinates.removeTextChangedListener(maskedListener)
        etCoordinates.onFocusChangeListener = null
    }

    //Suggestion RecyclerView callback
    override fun onSuggestionClicked(data: AddressModel) {
        //Set selected suggestion to VM
        viewModel.selectAddress(data)
    }

    override fun onSuggestionLongClicked(data: AddressModel) {
        //Set selected suggestion to EditText for completion
        viewModel.selectAddress(
            addressModel = data,
            forceSelection = true
        )
    }

    companion object {

        const val KEY_ADDRESS = "address"

    }

}