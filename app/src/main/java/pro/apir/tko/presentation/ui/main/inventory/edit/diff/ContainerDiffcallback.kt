package pro.apir.tko.presentation.ui.main.inventory.edit.diff

import androidx.recyclerview.widget.DiffUtil
import pro.apir.tko.presentation.entities.Container

/**
 * Created by Антон Сарматин
 * Date: 05.02.2020
 * Project: tko-android
 */
class ContainerDiffcallback(
        private val oldList: List<Container>,
        private val newList: List<Container>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition] || oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].volume == newList[newItemPosition].volume)
                && (oldList[oldItemPosition].type == newList[newItemPosition].type)

    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
}