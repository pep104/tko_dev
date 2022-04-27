package pro.apir.tko.presentation.ui.main.inventory.edit.diff

import androidx.recyclerview.widget.DiffUtil
import pro.apir.tko.presentation.entities.PhotoWrapper

/**
 * Created by Антон Сарматин
 * Date: 24.01.2020
 * Project: tko-android
 */
class ContainerEditImagesDiffCallback(
        private val oldList: List<PhotoWrapper>,
        private val newList: List<PhotoWrapper>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].new == newList[newItemPosition].new || oldList[oldItemPosition].uploaded?.image == newList[newItemPosition].uploaded?.image
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size


}