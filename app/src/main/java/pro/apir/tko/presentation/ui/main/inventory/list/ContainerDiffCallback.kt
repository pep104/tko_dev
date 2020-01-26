package pro.apir.tko.presentation.ui.main.inventory.list

import androidx.recyclerview.widget.DiffUtil
import pro.apir.tko.domain.model.ContainerAreaListModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-26
 * Project: tko-android
 */
class ContainerDiffCallback(
        private val oldList: List<ContainerAreaListModel>,
        private val newList: List<ContainerAreaListModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].id == newList[newItemPosition].id) && (oldList[oldItemPosition].location == newList[newItemPosition].location)
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

}