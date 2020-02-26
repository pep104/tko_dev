package pro.apir.tko.presentation.ui.main.route.navigation

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_route_navigation.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 10.02.2020
 * Project: tko-android
 *
 */
class RouteNavigationFragment : BaseFragment() {

    private val viewModel: RouteNavigationViewModel by viewModels()

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

        btnAction = view.btnAction

    }

    private fun observeViewModel(){



    }

    private fun test(){

        // btnAction.backgroundTintList = ContextCompat.getColorStateList(context!!, R.color.selector_button_light_colors )

    }

}