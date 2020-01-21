package pro.apir.tko.presentation.ui.main.address

import androidx.fragment.app.viewModels
import pro.apir.tko.R
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

}