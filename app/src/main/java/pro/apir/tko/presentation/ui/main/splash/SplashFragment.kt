package pro.apir.tko.presentation.ui.main.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import pro.apir.tko.R
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.GlobalState

class SplashFragment : BaseFragment() {

    override fun layoutId() = R.layout.fragment_splash

    override fun handleFailure(): LiveData<Failure>? = viewModel.failure

    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectSplashFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    //TODO HANDLE AUTH STATE AND TOKEN REFRESH
    private fun observeViewModel(){
        viewModel.state.observe(viewLifecycleOwner, Observer {
            when(it){
                is GlobalState.UserState.Authenticated -> {
                    findNavController().navigate(R.id.action_splashFragment_to_menuFragment)
                }
                is GlobalState.UserState.LoginNeeded -> {
                   findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            }
        })
    }


}
