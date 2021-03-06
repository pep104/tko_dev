package pro.apir.tko.presentation.ui.main.menu

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_menu.view.*
import kotlinx.android.synthetic.main.toolbar_title.view.textToolbarTitle
import kotlinx.android.synthetic.main.toolbar_title_exit.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
@AndroidEntryPoint
class MenuFragment : BaseFragment() {

    private val viewModel: MenuViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_menu

    override fun handleFailure() = viewModel.failure

    private lateinit var btnInventory: MaterialButton
    private lateinit var btnRoutes: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.textToolbarTitle.text = getString(R.string.title_menu)
        view.btnToolbarLogOut.setOnClickListener {
            globalState.logOut()
        }

        btnInventory = view.btnInventory.apply { this.isEnabled = false }
        btnRoutes = view.btnRoutes.apply { this.isEnabled = false }

        btnInventory.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_inventoryListFragment)
        }

        btnRoutes.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_routeListFragment)
        }


        Dexter.withActivity(activity).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                btnRoutes.isEnabled = true
                btnInventory.isEnabled = true
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?,
            ) {
                token?.continuePermissionRequest()
            }
        }).check()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

}