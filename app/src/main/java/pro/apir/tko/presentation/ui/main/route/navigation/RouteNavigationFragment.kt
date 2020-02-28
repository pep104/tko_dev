package pro.apir.tko.presentation.ui.main.route.navigation

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_route_navigation.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.route.RouteDetailedViewModel

/**
 * Created by Антон Сарматин
 * Date: 10.02.2020
 * Project: tko-android
 *
 */
class RouteNavigationFragment : BaseFragment() {

    //FIXME Not injecting? May be ok with default viewmodel provider factory????
    private val viewModel: RouteDetailedViewModel by navGraphViewModels(R.id.graphRoute)

    override fun layoutId() = R.layout.fragment_route_navigation

    override fun handleFailure() = viewModel.failure

    private lateinit var btnAction: MaterialButton

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
    //adapter
    //llmanager

    //TODO CONTROLS?

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

        btnAction = view.btnAction

    }

    private fun observeViewModel() {

        viewModel.currentStop.observe(viewLifecycleOwner, Observer {
            it?.let {
                setCardviewData(it)
                setState(it)
            }
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

        //TODO DISTANCE
        textDistance.text = getString(R.string.text_route_point_distance, 0.toString())
    }

    private fun setState(point: RoutePointModel) {
        //SET CARD BACK COLOR
        //SET TEXT COLOR
        //SET ICON COLOR
        //SET BTN ARROW COLOR
        //SET DIVIDER COLOR
        //SET BTN ACTION COLOR
        when (point.type) {
            RouteStateConstants.POINT_TYPE_DEFAULT -> {
                btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_default)

                cardPoint.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                textLocationHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPointHeaderGrey))
                textContainerInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                textContainerNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                textDistance.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                dividerPrevious.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.defaultRouteDivider))
                dividerNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.defaultRouteDivider))

                //TODO ICONS

                btnNext.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right_blue))
                btnPrevious.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_left_blue))
            }
            RouteStateConstants.POINT_TYPE_PENDING -> {
                btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_pending)

                //TODO

            }
            RouteStateConstants.POINT_TYPE_COMPLETED -> {
                btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_completed)

                //TODO

            }
        }
    }


    private fun setPhotosRecycler() {
        //TODO ?
    }

    private fun test() {

        // btnAction.backgroundTintList = ContextCompat.getColorStateList(context!!, R.color.selector_button_light_colors )

    }

}