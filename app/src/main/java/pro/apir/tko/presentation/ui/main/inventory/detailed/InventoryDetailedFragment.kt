package pro.apir.tko.presentation.ui.main.inventory.detailed

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by antonsarmatin
 * Date: 2020-01-19
 * Project: tko-android
 */
class InventoryDetailedFragment : BaseFragment() {

    private val viewModel: InventoryDetailedViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_inventory_detailed

    override fun handleFailure() = viewModel.failure

    private lateinit var textHeader: TextView
    private lateinit var textContainerInfo: TextView
    private lateinit var textContainerNumber: TextView

    private lateinit var imgClose: ImageView

    private lateinit var btnBack: ImageView
    private lateinit var btnSearch: ImageView

    private lateinit var imageRecyclerView: RecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}