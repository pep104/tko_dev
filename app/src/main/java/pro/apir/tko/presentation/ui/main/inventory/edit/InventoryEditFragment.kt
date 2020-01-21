package pro.apir.tko.presentation.ui.main.inventory.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.toolbar_back_title.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class InventoryEditFragment : BaseFragment() {

    private val viewModel: InventoryEditViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_inventory_edit

    override fun handleFailure() = viewModel.failure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryEditFragment(this)

        arguments?.let {
            //TODO TO VM
            val container = it.getParcelable(KEY_CONTAINER) as ContainerAreaDetailedModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isNewMode.observe(viewLifecycleOwner, Observer {
            if (it) {
                view.textToolbarTitle.text = getString(R.string.title_container_new)
            } else {
                view.textToolbarTitle.text = getString(R.string.title_container_edit)
            }
        })

    }


    companion object {
        const val KEY_CONTAINER = "container"
    }

}