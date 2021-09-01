package pro.apir.tko.presentation.ui.main.login

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.toolbar_title.view.*
import pro.apir.tko.BuildConfig
import pro.apir.tko.R
import pro.apir.tko.presentation.entities.HostUi
import pro.apir.tko.presentation.extension.getTextValue
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var etMail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tiHost: TextInputLayout

    private lateinit var loading: ProgressBar

    override fun layoutId() = R.layout.fragment_login

    override fun handleFailure() = viewModel.failure

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.textToolbarTitle.text = getString(R.string.title_auth)

        etMail = view.etLogin
        etPass = view.etPass
        btnLogin = view.btnLogin
        loading = view.progressBar
        tiHost = view.menuHost


        if(BuildConfig.DEBUG){
            etMail.setText("admin@apir.pro")
            etPass.setText("JsXkQCBv758uxWK92iRY")
        }

        btnLogin.setOnClickListener {
            viewModel.login(
                email = etMail.getTextValue(),
                pass = etPass.getTextValue(),
                host = tiHost.editText?.getTextValue() ?: ""
            )
        }

        setupHostPicker(tiHost)
        observeViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun observeViewModel() {
        viewModel.requestState.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            btnLogin.isEnabled = !it
            etPass.isEnabled = !it
            etMail.isEnabled = !it
            tiHost.isEnabled = !it
            loading.isVisible = it
        })

        viewModel.host.observe(viewLifecycleOwner, Observer {
            (tiHost.editText as AutoCompleteTextView)
                .setText(
                    getString(it.stringId),
                    false
                )
        })
    }

    private fun setupHostPicker(tiHost: TextInputLayout) {
        val items = HostUi.values().map { requireContext().getString(it.stringId) }
        val adapter = ArrayAdapter(requireContext(), R.layout.item_host, items)

        (tiHost.editText as AutoCompleteTextView).inputType = InputType.TYPE_NULL
        (tiHost.editText as AutoCompleteTextView).setAdapter(adapter)
    }

}