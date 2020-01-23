package pro.apir.tko.presentation.ui.main.address

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_address.view.*
import kotlinx.android.synthetic.main.fragment_inventory_edit.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.domain.model.SuggestionModel
import pro.apir.tko.presentation.extension.*
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class AddressFragment : BaseFragment(), SuggestionsAdapter.OnItemClickListener {

    private val viewModel: AddressViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_address

    override fun handleFailure() = viewModel.failure

    private lateinit var cardBottom: CardView
    private lateinit var cardSearch: CardView

    private lateinit var etAddress: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: SuggestionsAdapter

    private lateinit var btnSave: MaterialButton
    private lateinit var btnClearAddress: ImageView
    private lateinit var btnClearCoordinates: ImageView

    private lateinit var mapView: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null

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

        arguments?.let { bundle ->
            if (bundle.containsKey(KEY_ADDRESS)) {
                //todo set to vm?
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardBottom = view.cardBottom
        cardSearch = view.cardSearch

        recyclerView = view.recyclerView
        suggestionAdapter = SuggestionsAdapter()
        suggestionAdapter.setListener(this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = suggestionAdapter

        etAddress = view.etAddress
        btnSave = view.btnSave
        btnClearAddress = view.btnClearAddress

        mapView = view.map
        setMap(mapView)

        etAddress.addTextChangedListener(addressWatcher)

        btnClearAddress.setOnClickListener {
            etAddress.setText("")
        }

        view.btnSearch.setOnClickListener {
            setViewState(1)
        }

        requireActivity()
                .onBackPressedDispatcher
                .addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (cardSearch.isVisible()) {
                            setViewState(0)
                        } else {
                            findNavController().navigateUp()
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
    }

    private fun setMap(mapView: MapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.controller.zoomTo(12.0, 0L)

        val locationProvider = GpsMyLocationProvider(context)
        myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)
        myLocationOverlay?.enableFollowLocation()

        mapView.overlayManager.add(myLocationOverlay)
    }

    override fun onAddressItemChoosed(data: SuggestionModel) {
        viewModel.setChoosed(data)
        setViewState(0)
    }

    //TODO VM based
    private fun setViewState(type: Int){
        when(type){
            0 -> {
                cardSearch.gone()
                btnSave.visible()
                cardBottom.visible()
                hideKeyboard()
            }
            1 -> {
                cardBottom.gone()
                btnSave.gone()
                cardSearch.visible()
                etAddress.focusWithKeyboard()
            }
        }
    }

    companion object {

        const val KEY_ADDRESS = "address"

    }

}