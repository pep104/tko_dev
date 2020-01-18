package pro.apir.tko.presentation.ui.main.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_menu.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class MenuFragment : BaseFragment() {

    private val viewModel: MenuViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_menu

    override fun handleFailure() = viewModel.failure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectMenuFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.btnInventory.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_inventoryListFragment)
        }

        view.btnRoutes.setOnClickListener {
            //TODO navigate to routes list
        }
    }

}