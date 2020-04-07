package pro.apir.tko.presentation.ui.main.address

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_address.view.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
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

    private lateinit var cardBottom: CardView
    private lateinit var cardSearch: CardView

    private lateinit var textAddress: TextView
    private lateinit var textCoordinates: TextView

    private lateinit var etAddress: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: AddressSearchAdapter

    private lateinit var btnSave: MaterialButton
    private lateinit var btnClearAddress: ImageView
    private lateinit var btnClearCoordinates: ImageView

    private lateinit var mapView: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null

    private var addressPointOverlay: FolderOverlay? = null

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
                viewModel.setChoosed(bundle.get(KEY_ADDRESS) as AddressModel)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardBottom = view.cardBottom
        cardSearch = view.cardSearch

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

        btnSave = view.btnSave
        mapView = view.map
        setMap(mapView)



        btnClearAddress.setOnClickListener {
            etAddress.setText("")
        }

        view.btnSearch.setOnClickListener {
            viewModel.setViewType(AddressViewModel.ViewType.SEARCH)
        }

        view.textAddress.setOnClickListener {
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

        requireActivity()
                .onBackPressedDispatcher
                .addCallback(this, object : OnBackPressedCallback(true) {
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
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()
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
            etAddress.setText(it.value)
            if (it.lat != null && it.lng != null) {
                textCoordinates.isEnabled = true
                textCoordinates.text = getString(R.string.text_coordinates_placeholder, it.lat.toString(), it.lng.toString())
                myLocationOverlay?.disableFollowLocation()
                setMapPoint(it.lat, it.lng)
            } else {
                textCoordinates.isEnabled = false
                textCoordinates.text = getString(R.string.text_coordinates_not_found)
            }
        })

        viewModel.viewType.observe(viewLifecycleOwner, Observer {
            when (it) {
                AddressViewModel.ViewType.BOTTOM_CARD -> {
                    etAddress.removeTextChangedListener(addressWatcher)
                    hideKeyboard()
                    cardSearch.gone()
                    btnSave.visible()
                    cardBottom.visible()
                }
                AddressViewModel.ViewType.SEARCH -> {
                    etAddress.addTextChangedListener(addressWatcher)
                    cardBottom.gone()
                    btnSave.gone()
                    cardSearch.visible()
                    etAddress.focusWithKeyboard()
                    etAddress.placeCursorToEnd()

                }
                AddressViewModel.ViewType.LOCATION -> {
                    //TODO Location pick card
                }
            }
        })

    }

    //Configure map view
    private fun setMap(mapView: MapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.controller.zoomTo(12.0, 0L)

        val locationProvider = GpsMyLocationProvider(context)
        myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)

        if (viewModel.address.value == null) myLocationOverlay?.enableFollowLocation()

        mapView.overlayManager.add(myLocationOverlay)
    }

    //Set current map point from coordinates
    private fun setMapPoint(lat: Double, lng: Double) {

        mapView.controller.animateTo(GeoPoint(lat, lng))

        val location = GeoPoint(lat, lng)
        val marker = Marker(mapView)
        marker.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_map_pin)
        marker.setAnchor(Marker.ANCHOR_CENTER, 0.88f)
        marker.position = location

        val newOverlay = FolderOverlay().apply { add(marker) }

        mapView.overlayManager.remove(addressPointOverlay)
        mapView.overlayManager.add(newOverlay)
        addressPointOverlay = newOverlay

    }

    //Suggestion RecyclerView callback
    override fun onSuggestionSelected(data: AddressModel) {
        //Set selected suggestion to VM
        viewModel.setChoosed(data)
    }

    companion object {

        const val KEY_ADDRESS = "address"

    }

}