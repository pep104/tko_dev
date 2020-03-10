package pro.apir.tko.presentation.ui.main.route.navigation

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_route_navigation.view.*
import pro.apir.tko.R
import pro.apir.tko.core.extension.round
import pro.apir.tko.domain.model.PhotoModel
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.presentation.extension.gone
import pro.apir.tko.presentation.extension.visible
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


    private val viewModel: RouteDetailedViewModel by navGraphViewModels(R.id.graphRoute, { defaultViewModelProviderFactory })
    private val cameraSharedViewModel2: CameraSharedViewModel2 by activityViewModels()

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
    private lateinit var recyclerViewAdapter: RoutePointPhotoAttachAdapter

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

        recyclerViewPhotos = view.recyclerViewPhotos
        recyclerViewAdapter = RoutePointPhotoAttachAdapter().apply { setListener(this@RouteNavigationFragment) }
        with(recyclerViewPhotos) {
            adapter = recyclerViewAdapter
        }


        btnAction = view.btnAction

        observeViewModel()

        btnNext.setOnClickListener { viewModel.nextStop() }
        btnPrevious.setOnClickListener { viewModel.previousStop() }

    }

    override fun onDestroyView() {
        recyclerViewAdapter.setListener(null)
        super.onDestroyView()
    }

    private fun observeViewModel() {

        viewModel.currentStop.observe(viewLifecycleOwner, Observer {
            it?.let {
                setCardviewData(it)
                setPointState(it)
            }
        })

        viewModel.state.observe(viewLifecycleOwner, Observer {
            if (it == RouteDetailedViewModel.RouteState.InProgress) btnAction.visible()
            else btnAction.gone()
        })

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


    private fun setPointState(point: RoutePointModel) {

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
                btnAction.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.selector_button_action_state_pending)

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
        }
    }


    override fun onDeletePhoto(photo: PhotoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddNewPhoto() {
        //TODO OPEN CAMERA FRAGMENT
    }
}