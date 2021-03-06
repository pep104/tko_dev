package pro.apir.tko.presentation.ui.main.inventory.edit

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_inventory_edit.*
import kotlinx.android.synthetic.main.fragment_inventory_edit.view.*
import kotlinx.android.synthetic.main.toolbar_back_title.view.*
import pro.apir.tko.R
import pro.apir.tko.data.framework.dict.OptionsDictionariesManager
import pro.apir.tko.domain.model.ContainerAreaShortModel
import pro.apir.tko.domain.model.ContainerLoading
import pro.apir.tko.presentation.entities.Container
import pro.apir.tko.presentation.entities.PhotoWrapper
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.platform.view.PeekingLinearLayoutManager
import pro.apir.tko.presentation.ui.dialog.addcontainer.AddContainerDialog
import pro.apir.tko.presentation.ui.main.address.AddressFragment
import pro.apir.tko.presentation.ui.main.address.AddressSharedViewModel
import pro.apir.tko.presentation.ui.main.camera.CameraSharedViewModel
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
@AndroidEntryPoint
class InventoryEditFragment : BaseFragment(), ContainerAreaEditImagesAdapter.OnItemClickListener, AddContainerDialog.AddContainerListener, ContainersEditAdapter.OnContainerChangedListener {

    @Inject
    lateinit var optionsDictionariesManager: OptionsDictionariesManager

    private val viewModel: InventoryEditViewModel by viewModels()
    private val sharedAddressViewModel: AddressSharedViewModel by activityViewModels()
    private val sharedCameraViewModel: CameraSharedViewModel by activityViewModels()
    private val sharedEditViewModel: InventoryEditSharedViewModel by activityViewModels()
    private val sharedEditListViewModel: InventoryEditListSharedViewModel by activityViewModels()

    override fun layoutId() = R.layout.fragment_inventory_edit

    override fun handleFailure() = viewModel.failure

    private lateinit var adapterImages: ContainerAreaEditImagesAdapter
    private lateinit var recyclerViewImages: RecyclerView

    private lateinit var adapterContainers: ContainersEditAdapter
    private lateinit var recyclerViewContainers: RecyclerView

    private lateinit var etRegNum: EditText
    private lateinit var etArea: EditText
    private lateinit var etAddress: EditText
    private lateinit var textCoordinates: TextView

    private lateinit var spinnerAccess: Spinner
    private lateinit var adapterAccess: ArrayAdapter<String>
    private val spinnerAccessListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.access = position
        }
    }

    private lateinit var spinnerFence: Spinner
    private lateinit var adapterFence: ArrayAdapter<String>
    private val spinnerFenceListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.fence = position
        }
    }

    private lateinit var spinnerCoverage: Spinner
    private lateinit var adapterCoverage: ArrayAdapter<String>
    private val spinnerCoverageListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.coverage = position
        }
    }

    private lateinit var spinnerKgo: Spinner
    private lateinit var adapterKgo: ArrayAdapter<String>
    private val spinnerKgoListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.kgo = position
        }
    }

    private lateinit var spinnerHasCover: Spinner
    private lateinit var adapterHasCover: ArrayAdapter<String>
    private val spinnerHasCoverListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.hasCover = position
        }
    }

    private lateinit var spinnerInfoPlate: Spinner
    private lateinit var adapterInfoPlate: ArrayAdapter<String>
    private val spinnerInfoPlateListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.infoPlate = position
        }
    }

    private lateinit var textHeader: TextView
    private lateinit var btnAddPhoto: MaterialButton
    private lateinit var btnSave: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            if (bundle.containsKey(KEY_CONTAINER)) {
                bundle.getParcelable<ContainerAreaShortModel>(KEY_CONTAINER)?.let {
                    viewModel.setEditData(it)
                }
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

        textHeader = view.textHeader
        btnSave = view.btnSave
        btnAddPhoto = view.btnAddPhoto.also { it.isEnabled = false }
        progressBar = view.progressBar

        etRegNum = view.etRegNum
        etArea = view.etArea
        etAddress = view.etAdress.apply { keyListener = null }
        textCoordinates = view.textCoordinates

        spinnerAccess = view.spinnerAccess.apply { onItemSelectedListener = spinnerAccessListener }
        spinnerFence = view.spinnerFence.apply { onItemSelectedListener = spinnerFenceListener }
        spinnerCoverage = view.spinnerCover.apply { onItemSelectedListener = spinnerCoverageListener }
        spinnerKgo = view.spinnerKGO.apply { onItemSelectedListener = spinnerKgoListener }
        spinnerHasCover = view.spinnerHasCover.apply { onItemSelectedListener = spinnerHasCoverListener }
        spinnerInfoPlate = view.spinnerInfoPlate.apply { onItemSelectedListener = spinnerInfoPlateListener }

        recyclerViewImages = view.recyclerViewImages
        adapterImages = ContainerAreaEditImagesAdapter()
        adapterImages.setListener(this)
        recyclerViewImages.adapter = adapterImages
        recyclerViewImages.layoutManager = PeekingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewContainers = view.recycerViewContainers
        adapterContainers = ContainersEditAdapter(optionsDictionariesManager)
        adapterContainers.setListner(this)
        with(recyclerViewContainers) {
            adapter = adapterContainers
            layoutManager = LinearLayoutManager(context)
        }

        etRegNum.doAfterTextChanged {
            viewModel.registryNumber = it.toString()
        }

        etArea.doAfterTextChanged {
            if (!it.toString().isNullOrBlank())
                viewModel.area = it.toString().toDouble()
        }

        llAddress.setOnClickListener {
            openAddressFragment()
        }

        with(etAddress) {
            movementMethod = null
            keyListener = null
        }

        view.btnAddContainer.setOnClickListener {
            AddContainerDialog(this).show(childFragmentManager, "containerAdd")
        }

        view.btnToolbarBack.setOnClickListener(::back)

        btnSave.setOnClickListener {
            viewModel.save()
        }

        askForPemission()
        observeViewModel()
    }

    private fun observeViewModel() {

        //EDIT

        viewModel.images.observe(viewLifecycleOwner, Observer { images ->
            images?.let {
                adapterImages.setData(it)
            }
        })

        viewModel.address.observe(viewLifecycleOwner, Observer { address ->
            address?.let {
                setLocationViews(it.value, it.lat, it.lng)
            }
        })

        viewModel.area?.let {
            etArea.setText(it.toString())
        }

        viewModel.registryNumber?.let {
            etRegNum.setText(it)
        }

        viewModel.containers.observe(viewLifecycleOwner, Observer { containers ->
            containers?.let {
                titleContainers.visibility = if(it.isNotEmpty()) View.VISIBLE else View.GONE
                adapterContainers.setData(it)
            }
        })

        viewModel.accessOptions.observe(viewLifecycleOwner, Observer {
            adapterAccess = ArrayAdapter(requireContext(), R.layout.spinner_item, it.values.toList())
                    .apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }
            with(spinnerAccess) {
                adapter = adapterAccess
                val current = viewModel.access
                if (current != null && current < it.values.size)
                    setSelection(current)
                else
                    setSelection(0)
            }

        })

        viewModel.fenceOptions.observe(viewLifecycleOwner, Observer {
            adapterFence = ArrayAdapter(requireContext(), R.layout.spinner_item, it.values.toList())
                    .apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }
            with(spinnerFence) {
                adapter = adapterFence
                val current = viewModel.fence
                if (current != null && current < it.values.size)
                    setSelection(current)
                else
                    setSelection(0)
            }
        })

        viewModel.coverageOptions.observe(viewLifecycleOwner, Observer {
            adapterCoverage = ArrayAdapter(requireContext(), R.layout.spinner_item, it.values.toList())
                    .apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }
            with(spinnerCoverage) {
                adapter = adapterCoverage
                val current = viewModel.coverage
                if (current != null && current < it.values.size)
                    setSelection(current)
                else
                    setSelection(0)
            }
        })

        viewModel.kgoOptions.observe(viewLifecycleOwner, Observer {
            adapterKgo = ArrayAdapter(requireContext(), R.layout.spinner_item, it.values.toList())
                    .apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }
            with(spinnerKgo) {
                adapter = adapterKgo
                val current = viewModel.kgo
                if (current != null && current < it.values.size)
                    setSelection(current)
                else
                    setSelection(0)
            }
        })

        viewModel.hasCoverOptions.observe(viewLifecycleOwner, Observer {
            adapterHasCover = ArrayAdapter(requireContext(), R.layout.spinner_item, it.values.toList())
                    .apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }
            with(spinnerHasCover) {
                adapter = adapterHasCover
                val current = viewModel.hasCover
                if (current != null && current < it.values.size)
                    setSelection(current)
                else
                    setSelection(0)
            }
        })

        viewModel.infoPlateOptions.observe(viewLifecycleOwner, Observer {
            adapterInfoPlate = ArrayAdapter(requireContext(), R.layout.spinner_item, it.values.toList())
                    .apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }
            with(spinnerInfoPlate) {
                adapter = adapterInfoPlate
                val current = viewModel.infoPlate
                if (current != null && current < it.values.size)
                    setSelection(current)
                else
                    setSelection(0)
            }
        })


        //SHARED

        sharedAddressViewModel.address.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.setAddress(it)
                sharedAddressViewModel.consume()
            }
        })

        sharedCameraViewModel.photos.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.addNewPhotos(it)
                sharedCameraViewModel.consume()
            }
        })

        //STATUS

        viewModel.isSaved.observe(viewLifecycleOwner, Observer {
            it?.let { result ->

                //Если площадка создана, то обновляем только List Shared VM,
                // если обновлена, то обе Shared VM
                if (result is EditResultEvent.Created) {
                    sharedEditListViewModel.setResult(result)
                } else {
                    sharedEditListViewModel.setResult(result)
                    sharedEditViewModel.setResult(result)
                }

                findNavController().navigateUp()
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            progressBar.isInvisible = !it
            etRegNum.isEnabled = !it
            etAddress.isEnabled = !it

            spinnerAccess.isEnabled = !it
            spinnerFence.isEnabled = !it
            spinnerCoverage.isEnabled = !it
            spinnerKgo.isEnabled = !it
            spinnerHasCover.isEnabled = !it
            spinnerInfoPlate.isEnabled = !it

            btnSave.isEnabled = !it
        })


    }

    override fun onDestroyView() {
        adapterImages.setListener(null)
        super.onDestroyView()
    }

    private fun setLocationViews(location: String?, lat: Double?, lng: Double?) {
        etAddress.setText(location)
        textCoordinates.text = getString(R.string.text_coordinates_placeholder, lat.toString(), lng.toString())
    }

    override fun onImageDeleteClicked(item: PhotoWrapper) {
        viewModel.deletePhoto(item)
    }

    override fun onNewContainerAdded(list: List<Container>) {
        viewModel.addContainers(list)
    }

    override fun onContainerDataChanged(id: Int?, type: String, loading: ContainerLoading, volume: Double?) {
        viewModel.updateContainer(id, type, loading, volume)
    }

    private fun openAddressFragment() {
        //Temp
        val address = viewModel.address.value
        val location = address?.value
        val lat = address?.lat
        val lng = address?.lng
        if (address != null && location != null) {
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