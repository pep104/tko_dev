package pro.apir.tko.presentation.ui.main.inventory.edit

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_inventory_edit.view.*
import kotlinx.android.synthetic.main.toolbar_back_title.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.presentation.entities.AddressEntity
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.platform.view.PeekingLinearLayoutManager
import pro.apir.tko.presentation.ui.main.address.AddressFragment
import pro.apir.tko.presentation.ui.main.address.AddressFragment_MembersInjector
import pro.apir.tko.presentation.ui.main.address.AddressSharedViewModel
import java.util.jar.Manifest

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class InventoryEditFragment : BaseFragment(), ContainerEditImagesAdapter.OnItemClickListener {

    private val viewModel: InventoryEditViewModel by viewModels()
    private val sharedViewModel: AddressSharedViewModel by activityViewModels()

    override fun layoutId() = R.layout.fragment_inventory_edit

    override fun handleFailure() = viewModel.failure

    private lateinit var adapter: ContainerEditImagesAdapter
    private lateinit var recyclerView: RecyclerView

    //TODO TO COMPOUND VIEW?
    private lateinit var etRegNum: EditText
    private lateinit var textAddress: TextView
    private lateinit var textCoordinates: TextView

    private lateinit var btnAddPhoto: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryEditFragment(this)

        arguments?.let { bundle ->
            if (bundle.containsKey(KEY_CONTAINER)) {
                val container: ContainerAreaShortModel? = bundle.getParcelable(KEY_CONTAINER) as ContainerAreaShortModel
                container?.let { viewModel.setEditData(it) }
            }
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

        btnAddPhoto = view.btnAddPhoto.also { it.isEnabled = false }

        etRegNum = view.et
        textAddress = view.textAddress
        textCoordinates = view.textCoordinates

        recyclerView = view.recyclerView
        adapter = ContainerEditImagesAdapter()
        adapter.setListener(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = PeekingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        etRegNum.doAfterTextChanged {
            viewModel.updateRegNum(it.toString())
        }

        view.layoutAddress.setOnClickListener {
            openAddressFragment()
        }

        view.btnToolbarBack.setOnClickListener(::back)

        view.btnSave.setOnClickListener {
            viewModel.save()
        }

        askForPemission()
        observeViewModel()
    }

    private fun observeViewModel() {

        viewModel.containerArea.observe(viewLifecycleOwner, Observer { data ->
            data.registryNumber?.let {
                etRegNum.setText(it)
            }

            data.location?.let {
                setLocationViews(data.location, data.coordinates?.lat, data.coordinates?.lng)
            }
        })

        viewModel.images.observe(viewLifecycleOwner, Observer { images ->
            images.let {
                adapter.setData(it)
            }
        })

        sharedViewModel.address.observe(viewLifecycleOwner, Observer {
            setLocationViews(it.value, it.lat, it.lng)
            viewModel.updateAddress(it)
        })
    }

    override fun onDestroyView() {
        adapter.setListener(null)
        super.onDestroyView()
    }

    private fun setLocationViews(location: String?, lat: Double?, lng: Double?) {
        textAddress.text = location
        textCoordinates.text = getString(R.string.text_coordinates_placeholder, lat.toString(), lng.toString())
    }

    override fun onImageDeleteClicked(image: Int) {
        viewModel.deletePhoto(image)
    }


    private fun openAddressFragment() {
        //FIXME
        //Temp
        val container = viewModel.containerArea.value
        val location = container?.location
        val coordinates = container?.coordinates
        if (location != null && coordinates != null) {
            val address = AddressModel(location, location, coordinates.lat, coordinates.lng)
            findNavController().navigate(R.id.action_inventoryEditFragment_to_addressFragment, bundleOf(AddressFragment.KEY_ADDRESS to address))
        } else {
            findNavController().navigate(R.id.action_inventoryEditFragment_to_addressFragment)
        }

    }

    private fun askForPemission() {
        Dexter.withActivity(activity).withPermission(android.Manifest.permission.CAMERA).withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                btnAddPhoto.apply {
                    isEnabled = true
                    setOnClickListener {
                        findNavController().navigate(R.id.action_inventoryEditFragment_to_cameraFragment)
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {

            }
        }).check()
    }

    companion object {
        const val KEY_CONTAINER = "container"
    }

}