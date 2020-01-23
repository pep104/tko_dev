package pro.apir.tko.presentation.ui.main.inventory.detailed

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.bottomsheet_inventory_detailed.view.*
import kotlinx.android.synthetic.main.content_inventory_detailed.view.*
import kotlinx.android.synthetic.main.fragment_inventory_detailed.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.extension.goneWithFade
import pro.apir.tko.presentation.extension.visible
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.inventory.edit.InventoryEditFragment

/**
 * Created by antonsarmatin
 * Date: 2020-01-19
 * Project: tko-android
 */
//TODO ADD EDIT BUTTON
class InventoryDetailedFragment : BaseFragment() {

    private val viewModel: InventoryDetailedViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_inventory_detailed

    override fun handleFailure() = viewModel.failure

    private lateinit var loading: ProgressBar

    private lateinit var textHeader: TextView
    private lateinit var textContainerInfo: TextView
    private lateinit var textContainerNumber: TextView

    private lateinit var imgClose: ImageView
    private lateinit var imgThrash: ImageView

    private lateinit var btnBack: ImageView
    private lateinit var btnSearch: ImageView
    private lateinit var btnEdit: MaterialButton

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var adapter: ContainerImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.createMainComponent().injectInventoryDetailedFragment(this)

        arguments?.let {
            val id = it.getLong(KEY_ID, 0L)
            val header = it.getString(KEY_HEADER, "")
            viewModel.fetchInfo(id, header)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading = view.loading
        textHeader = view.textHeader
        textContainerInfo = view.textContainerInfo
        textContainerNumber = view.textContainerNumber

        imgThrash = view.imgThrash
        imgClose = view.imgClose
        btnBack = view.btnBack
        btnEdit = view.btnEdit

        imageRecyclerView = view.imageRecyclerView
        adapter = ContainerImagesAdapter()
        imageRecyclerView.adapter = adapter

        imgClose.setOnClickListener(::back)
        btnBack.setOnClickListener(::back)
        btnEdit.setOnClickListener { findNavController().navigate(R.id.action_inventoryDetailedFragment_to_inventoryEditFragment, bundleOf(InventoryEditFragment.KEY_CONTAINER to viewModel.data.value)) }

        observeViewModel()
    }


    private fun observeViewModel() {

        viewModel.data.observe(viewLifecycleOwner, Observer {
            btnEdit.isEnabled = true

            val pluredCount = resources.getQuantityString(
                    R.plurals.plurals_containers,
                    it.containersCount
                            ?: 0, it.containersCount)
            val area = it.area ?: 0.0

            textContainerInfo.text = getString(R.string.text_container_detailed_info, pluredCount, area.toString())

            loading.goneWithFade()
            imgThrash.visible()
        })

        viewModel.images.observe(viewLifecycleOwner, Observer {
            imageRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter.setData(it)
        })

        viewModel.header.observe(viewLifecycleOwner, Observer {
            textHeader.text = it
        })
    }

    companion object {

        const val KEY_ID = "id"
        const val KEY_HEADER = "header"

    }

}