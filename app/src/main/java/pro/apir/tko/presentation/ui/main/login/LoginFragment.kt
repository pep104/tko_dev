package pro.apir.tko.presentation.ui.main.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_login.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.extension.getTextValue
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
//TODO LOADING HANDLING
class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var etMail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: MaterialButton

    override fun layoutId() = R.layout.fragment_login

    override fun handleFailure() = viewModel.failure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectLoginFragment(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etMail = view.etLogin
        etPass = view.etPass
        btnLogin = view.btnLogin

        etMail.setText("admin@apir.pro")
        etPass.setText("JsXkQCBv758uxWK92iRY")
        btnLogin.setOnClickListener {
            viewModel.login(etMail.getTextValue(), etPass.getTextValue())
        }

        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.requestState.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
        })
    }

}