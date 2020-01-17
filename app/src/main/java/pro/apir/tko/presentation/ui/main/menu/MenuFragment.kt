package pro.apir.tko.presentation.ui.main.menu

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import pro.apir.tko.R
import pro.apir.tko.core.exception.Failure
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

}