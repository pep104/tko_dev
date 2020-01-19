package pro.apir.tko.presentation.ui.main.inventory.detailed

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import pro.apir.tko.R
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by antonsarmatin
 * Date: 2020-01-19
 * Project: tko-android
 */
class InventoryDetailedFragment : BaseFragment() {

    private val viewModel: InventoryDetailedViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_inventory_detailed

    override fun handleFailure() = viewModel.failure
}