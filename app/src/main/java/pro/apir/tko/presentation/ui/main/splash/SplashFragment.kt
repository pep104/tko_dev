package pro.apir.tko.presentation.ui.main.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pro.apir.tko.R
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.GlobalState

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    override fun layoutId() = R.layout.fragment_splash

    override fun handleFailure(): LiveData<Failure>? = viewModel.failure

    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setStatusBarColor(R.color.colorPrimary)
//        setStatusBarLightMode(false)
    }

    private fun observeViewModel(){
        viewModel.state.observe(viewLifecycleOwner, Observer {
            when(it){
                is GlobalState.UserState.Authenticated -> {
                    setStatusBarColor(R.color.white)
//                    setStatusBarLightMode(true)
                    findNavController().navigate(R.id.action_splashFragment_to_menuFragment)
                }
                is GlobalState.UserState.LoginNeeded -> {
                    setStatusBarColor(R.color.white)
//                    setStatusBarLightMode(true)
                   findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            }
        })
    }


}
