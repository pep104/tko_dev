package pro.apir.tko.presentation.ui.main.inventory.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_edit_container.view.*
import pro.apir.tko.R
import pro.apir.tko.data.framework.dict.OptionsDictionariesManager
import pro.apir.tko.domain.model.ContainerLoading
import pro.apir.tko.presentation.entities.Container
import pro.apir.tko.presentation.entities.ContainerLoadingUi
import pro.apir.tko.presentation.ui.main.inventory.edit.diff.ContainerDiffcallback

class ContainersEditAdapter(dictionariesManager: OptionsDictionariesManager) :
    RecyclerView.Adapter<ContainersEditAdapter.ContainerHolder>() {

    private val typeDictionary = dictionariesManager.getContainerTypeDictionary()

    private val data = arrayListOf<Container>()

    private var listener: OnContainerChangedListener? = null

    interface OnContainerChangedListener {

        fun onContainerDataChanged(
            id: Int?,
            type: String,
            loading: ContainerLoading,
            volume: Double?,
        )

    }

    fun setListner(listener: OnContainerChangedListener) {
        this.listener = listener
    }

    fun setData(data: List<Container>) {
        val diffcallback = ContainerDiffcallback(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffcallback)
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerHolder {
        return ContainerHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_edit_container, parent, false))
    }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: ContainerHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ContainerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var id: Int? = null
        private var pos: Int? = null

        private val spinnerType: Spinner
        private val spinnerTypeAdapter: ArrayAdapter<String> = ArrayAdapter(itemView.context,
            R.layout.spinner_item,
            typeDictionary.values.toList()).apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }

        private val spinnerLoading: Spinner
        private val spinnerLoadingAdapter: ArrayAdapter<String> = ArrayAdapter(
            itemView.context,
            R.layout.spinner_item,
            ContainerLoadingUi.values().map { itemView.context.getString(it.stringId) }
        ).apply { setDropDownViewResource(R.layout.spinner_item_dropdown) }


        private val spinnerTypeListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                pos?.let {
                    val keyType = typeDictionary.getKey(position)
                    if (keyType != null)
                        data[it].type = keyType

                    //TODO Прокидывать тольк изменения поля, а не изменять var и потом уведомлять об этом
                    listener?.onContainerDataChanged(
                        id = data[it].id,
                        type = data[it].type,
                        loading = data[it].loading,
                        volume = data[it].volume
                    )
                }
            }
        }

        private val spinnerLoadingListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                pos?.let {
                    data[it].loading = ContainerLoading.values()[position]

                    //TODO Прокидывать тольк изменения поля, а не изменять var и потом уведомлять об этом
                    listener?.onContainerDataChanged(
                        id = data[it].id,
                        type = data[it].type,
                        loading = data[it].loading,
                        volume = data[it].volume
                    )
                }
            }
        }

        private val etVolume: EditText

        init {
            etVolume = itemView.etVolume
            spinnerType = itemView.spinnerType
            spinnerType.adapter = spinnerTypeAdapter
            spinnerLoading = itemView.spinnerLoading
            spinnerLoading.adapter = spinnerLoadingAdapter
        }

        fun bind(item: Container, pos: Int) {
            id = item.id
            this.pos = pos
            spinnerType.setSelection(typeDictionary.getPositionByKey(item.type))
            spinnerType.onItemSelectedListener = spinnerTypeListener

            spinnerLoading.setSelection(item.loading.ordinal)
            spinnerLoading.onItemSelectedListener = spinnerLoadingListener

            item.volume?.let {
                etVolume.setText(item.volume.toString())
            }

        }

    }

}
