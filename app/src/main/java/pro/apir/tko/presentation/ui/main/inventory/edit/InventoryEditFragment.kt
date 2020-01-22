package pro.apir.tko.presentation.ui.main.inventory.edit

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_inventory_edit.view.*
import kotlinx.android.synthetic.main.toolbar_back_title.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaDetailedModel
import pro.apir.tko.presentation.extension.getTextValue
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.platform.view.PeekingLinearLayoutManager

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class InventoryEditFragment : BaseFragment(), ContainerEditImagesAdapter.OnItemClickListener {

    private val viewModel: InventoryEditViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_inventory_edit

    override fun handleFailure() = viewModel.failure

    private lateinit var adapter: ContainerEditImagesAdapter
    private lateinit var recyclerView: RecyclerView

    //TODO TO COMPOUND VIEW?
    private lateinit var etRegNum: EditText
    private lateinit var textAddress: TextView
    private lateinit var textCoordinates: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryEditFragment(this)

        arguments?.let { bundle ->
            if (bundle.containsKey(KEY_CONTAINER)) {
                val container: ContainerAreaDetailedModel? = bundle.getParcelable(KEY_CONTAINER) as ContainerAreaDetailedModel
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

        etRegNum = view.et
        textAddress = view.textAddress
        textCoordinates = view.textCoordinates

        recyclerView = view.recyclerView
        adapter = ContainerEditImagesAdapter()
        adapter.setListener(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = PeekingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        view.layoutAddress.setOnClickListener {
            //TODO PUT ADDRESS DATA IF EXISTS
            findNavController().navigate(R.id.action_inventoryEditFragment_to_addressFragment)
        }

        view.btnToolbarBack.setOnClickListener(::back)
        observeViewModel()

        view.btnSave.setOnClickListener {
            viewModel.testSave(etRegNum.getTextValue(), textAddress.text.toString())
        }
    }

    private fun observeViewModel() {

        viewModel.containerArea.observe(viewLifecycleOwner, Observer {
            etRegNum.setText(it.registry_number)
            textAddress.text = it.location
            textCoordinates.text = getString(R.string.text_coordinates_placeholder, it.coordinates?.lat.toString(), it.coordinates?.lng.toString())
        })

        viewModel.images.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })

    }

    override fun onDestroyView() {
        adapter.setListener(null)
        super.onDestroyView()
    }

    override fun onImageDeleteClicked(image: Int) {
        viewModel.deletePhoto(image)
    }

    companion object {
        const val KEY_CONTAINER = "container"
    }

}