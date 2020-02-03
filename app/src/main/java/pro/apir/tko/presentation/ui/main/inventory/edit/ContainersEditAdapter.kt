package pro.apir.tko.presentation.ui.main.inventory.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_edit_container.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.dict.OptionsDictionariesManager
import pro.apir.tko.presentation.entities.Container
import javax.inject.Inject

class ContainersEditAdapter : RecyclerView.Adapter<ContainersEditAdapter.ContainerHolder>() {

    @Inject
    private lateinit var optionsDictionariesManager: OptionsDictionariesManager

    private val data = arrayListOf<Container>()

    interface OnContainerChangedListener{

        //TODO CONTAINER ACTIONS

    }

    fun setData(data: List<Container>) {
        this.data.clear()
        this.data.addAll(data)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerHolder {
        return ContainerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_edit_container, parent, false))
    }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: ContainerHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ContainerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val etCount: EditText

        private val spinnerType: Spinner
        private val spinnerTypeAdapter: ArrayAdapter<String>
        private val spinnerTypeListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //TODO
            }
        }

        private val etVolume: EditText

        init {
            etCount = itemView.etCount
            etVolume = itemView.etVolume
            spinnerType = itemView.spinnerType
            spinnerTypeAdapter = ArrayAdapter(spinnerType.context, R.layout.spinner_item, optionsDictionariesManager.getContainerTypeDictionary().values.toList())
        }

        fun bind(item: Container, pos: Int) {

            //todo set spinner and find item?

            //todo bind views

        }

    }

}
