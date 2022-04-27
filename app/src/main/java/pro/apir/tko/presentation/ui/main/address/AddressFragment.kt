package pro.apir.tko.presentation.ui.main.address

import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import com.redmadrobot.inputmask.model.Notation
import dagger.hilt.android.AndroidEntryPoint
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
@AndroidEntryPoint
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
    private lateinit var pbAddressCoordinates: ProgressBar

    private val mapListener: MapListener = object : MapListener {
        override fun onScroll(event: ScrollEvent?): Boolean {
            val lat = mapView.mapCenter.latitude.round(6)
            val lon = mapView.mapCenter.longitude.round(6)
            viewModel.updateCoordinatesOnDragEvent(lat, lon)
            setMapPoint(
                lat = lat,
                lng = lon,
                move = false,
                center = false
            )
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
                    formattedValue: String,
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

        //Try to get AddressModel parcelable
        arguments?.let { bundle ->
            if (bundle.containsKey(KEY_ADDRESS)) {
                viewModel.selectAddress(
                    addressModel = bundle.get(KEY_ADDRESS) as AddressModel,
                    forceSelection = true,
                    forceQuery = true
                )
            } else {
                viewModel.selectUser()
            }
        } ?: viewModel.selectUser()

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
        pbAddressCoordinates = view.pbAddressCoordinates

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
            viewModel.submitCoordinatesUpdate()
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

        if (viewModel.address.value == null && viewModel.lastPosition != null)
            mapView.controller.setCenter(GeoPoint(viewModel.lastPosition))
    }

    override fun onPause() {
        mapView.onPause()
        myLocationOverlay?.disableMyLocation()
        super.onPause()
    }

    private fun observeViewModel() {
        viewModel.suggestions.observe(viewLifecycleOwner, suggestionAdapter::setList)

        viewModel.address.observe(viewLifecycleOwner, ::handleAddress)

        viewModel.addressCoordinatesChanger.observe(viewLifecycleOwner,
            ::handleAddressCoordinatesChanger)

        viewModel.addressCoordinatesLoading.observe(viewLifecycleOwner,
            ::handleAddressCordinatesLoading)

        viewModel.viewType.observe(viewLifecycleOwner, ::handleViewType)

        //coordinates edit
        viewModel.errorCoordinates.observe(viewLifecycleOwner) { isError ->
            textErrorCoordinates.isVisible = isError
        }


        //controls etc
        viewModel.isFollowEnabled.observe(viewLifecycleOwner) { isFollowEnabled ->
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

                    viewModel.address.value.withCoordinates { lat, lon ->
                        setMapPoint(lat, lon)
                    }

                }

            }
        }

    }

    private fun handleViewType(type: AddressViewModel.ViewType) {
        removeCoordinatesMaskedTextListener()
        mapView.removeMapListener(mapListener)
        textTitle.text = getString(R.string.text_address_title)
        when (type) {
            AddressViewModel.ViewType.BOTTOM_CARD -> {
                etAddress.removeTextChangedListener(addressWatcher)
                hideKeyboard()
                cardSearch.gone()
                cardCoordinates.gone()
                btnSave.visible()
                cardBottom.visible()

                if (viewModel.address.value == null && viewModel.lastPosition != null)
                    mapView.controller.setCenter(GeoPoint(viewModel.lastPosition))

                viewModel.address.value.withCoordinates { lat, lon ->
                    setMapPoint(lat, lon, false)
                }


            }
            AddressViewModel.ViewType.SEARCH -> {
                viewModel.address.value?.let { addressModel ->
                    etAddress.setText(addressModel.value)
                }
                etAddress.addTextChangedListener(addressWatcher)

                cardBottom.invisible()
                btnSave.invisible()
                cardSearch.visible()
                etAddress.focusWithKeyboard()
                etAddress.placeCursorToEnd()

            }
            AddressViewModel.ViewType.LOCATION_COORDINATES -> {
                viewModel.address.value.withCoordinates { lat, lon ->
                    mapView.controller.setCenter(GeoPoint(lat, lon))
                }

                textTitle.text = getString(R.string.text_address_title_coordinates)
                mapView.addMapListener(mapListener)
                btnSave.invisible()
                cardBottom.invisible()
                cardCoordinates.visible()

                val address = viewModel.addressCoordinatesChanger.value
                if (address?.lat != null && address.lng != null) {
                    val coordinatesText = getString(
                        R.string.text_coordinates_placeholder,
                        address.lat.toString(),
                        address.lng.toString()
                    )
                    etCoordinates.setText(coordinatesText)
                    etCoordinates.hint = coordinatesText
                } else {
                    etCoordinates.setText("")
                    etCoordinates.hint = getString(R.string.hint_address_coordinates_input)
                }

                setCoordinatesMaskedTextListener()
            }
        }
    }

    private fun handleAddress(address: AddressModel) {

        textAddress.text = address.value
        etAddress.applyUnwatchable(addressWatcher) {
            setText(address.value)
            setSelection(address.value.length)
        }

        textAddress.isEnabled = !address.isUserLocation


        if (address.lat != null && address.lng != null) {
            textCoordinates.isEnabled = true
            val coordinatesText = getString(
                R.string.text_coordinates_placeholder,
                address.lat.toString(),
                address.lng.toString()
            )
            textCoordinates.text = coordinatesText
            myLocationOverlay?.disableFollowLocation()
            btnCopy.visible()
            setMapPoint(address.lat!!, address.lng!!)
        } else {
            textCoordinates.isEnabled = false
            textCoordinates.text = getString(R.string.text_coordinates_not_found)
            etCoordinates.setText("")
        }
    }

    private fun handleAddressCoordinatesChanger(address: AddressModel?) {
        textAddressCoordinates.isEnabled = address != null
        btnSubmitCoordinates.isEnabled = address != null
        textAddressCoordinates.text = address?.value ?: getString(R.string.text_address_not_found)

        textAddressCoordinates.isEnabled = address?.isUserLocation == false
    }

    private fun handleAddressCordinatesLoading(isLoading: Boolean) {
        if(btnSubmitCoordinates.isEnabled){
            btnSubmitCoordinates.isEnabled = !isLoading
        }
        pbAddressCoordinates.isVisible = isLoading
    }

    //Configure map view
    private fun setMap(mapView: MapView) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.zoomTo(17.0, 0L)

        viewModel.lastPosition?.let {
            mapView.controller.setCenter(it)
        }

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
    private fun setMapPoint(
        lat: Double,
        lng: Double,
        move: Boolean = true,
        center: Boolean = true
    ) {
        if (move)
            mapView.controller.animateTo(GeoPoint(lat, lng))
        else if (center)
            mapView.controller.setCenter(GeoPoint(lat, lng))

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

    //Utils
    private fun AddressModel?.withCoordinates(func: (Double, Double) -> Unit) {
        this?.let {
            if (lat != null && lng != null)
                func(lat!!, lng!!)
        }
    }

    companion object {

        const val KEY_ADDRESS = "address"

    }

}