package pro.apir.tko.presentation.ui.main.list.container

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container_list.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerAreaListModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class ContainerListAdapter : RecyclerView.Adapter<ContainerListAdapter.ContainerHolder>() {

    private val data = arrayListOf<ContainerAreaListModel>()

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {

        fun onContainerItemClicked(item: ContainerAreaListModel)

    }

    fun setList(list: List<ContainerAreaListModel>) {
        val diffCallback = ContainerDiffCallback(this.data, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerHolder {
        return ContainerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_container_list, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ContainerHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ContainerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textHeader: TextView
        private val textMessage: TextView

        init {
            textHeader = itemView.textHeader
            textMessage = itemView.textMessage
        }

        fun bind(item: ContainerAreaListModel, pos: Int) {

            textHeader.text = item.location
            val pluredCount = textMessage.context.resources.getQuantityString(R.plurals.plurals_containers, item.containersCount
                    ?: 0, item.containersCount)
            val area = item.area ?: 0.0
            textMessage.text = textMessage.context.getString(R.string.text_list_container_message, item.registryNumber, pluredCount, area.toInt().toString())

            itemView.setOnClickListener {
                listener?.onContainerItemClicked(item)
            }

        }

    }

}