package pro.apir.tko.presentation.ui.main.address

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_address.view.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.presentation.extension.gone
import pro.apir.tko.presentation.extension.isVisible
import pro.apir.tko.presentation.extension.visible
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class AddressFragment : BaseFragment() {

    private val viewModel: AddressViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_address

    override fun handleFailure() = viewModel.failure

    private lateinit var cardBottom: CardView
    private lateinit var cardSearch: CardView

    private lateinit var etAddress: EditText

    private lateinit var btnSave: MaterialButton

    private lateinit var mapView: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectAddressFragment(this)

        arguments?.let { bundle ->
            if (bundle.containsKey(KEY_ADDRESS)){
                //todo set to vm?
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardBottom = view.cardBottom
        cardSearch = view.cardSearch

        etAddress = view.etAddress
        btnSave = view.btnSave

        mapView = view.map
        setMap(mapView)

        view.btnSearch.setOnClickListener {
            //TODO FOCUS ET SEARCH
            //HIDE BOTTOMCARD
            //REVEAL TOPCARD
            cardBottom.gone()
            btnSave.gone()
            cardSearch.visible()

            //TODO FOCUS ET AND OPEN KEYBOARD
        }

        requireActivity()
                .onBackPressedDispatcher
                .addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (cardSearch.isVisible()) {
                            cardSearch.gone()
                            btnSave.visible()
                            cardBottom.visible()
                        } else {
                            findNavController().navigateUp()
                        }
                    }
                }
                )

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

    private fun setMap(mapView: MapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.controller.zoomTo(12.0, 0L)

        val locationProvider = GpsMyLocationProvider(context)
        myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)
        myLocationOverlay?.enableFollowLocation()

        mapView.overlayManager.add(myLocationOverlay)
    }

    companion object {

        const val KEY_ADDRESS = "address"

    }

}