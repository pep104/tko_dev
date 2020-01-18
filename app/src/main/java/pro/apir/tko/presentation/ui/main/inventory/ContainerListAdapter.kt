package pro.apir.tko.presentation.ui.main.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container_list.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.ContainerModel

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class ContainerListAdapter : RecyclerView.Adapter<ContainerListAdapter.ContainerHolder>() {

    private val data = arrayListOf<ContainerModel>()


    fun setList(list: List<ContainerModel>){
        //TODO diffutil
        this.data.clear()
        this.data.addAll(list)
        notifyItemRangeInserted(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerHolder {
        return ContainerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_container_list, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ContainerHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ContainerHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val textHeader: TextView
        private val textMessage: TextView

        init {
            textHeader = itemView.textHeader
            textMessage = itemView.textMessage
        }

        fun bind(item: ContainerModel, pos: Int){

            textHeader.text = item.location
            val pluredCount = textMessage.context.resources.getQuantityString(R.plurals.plurals_containers, item.containersCount ?: 0, item.containersCount)
            val area =  item.area ?: 0.0
            textMessage.text = textMessage.context.getString(R.string.text_list_container_message, item.registry_number, pluredCount, area.toInt().toString())

        }

    }

}