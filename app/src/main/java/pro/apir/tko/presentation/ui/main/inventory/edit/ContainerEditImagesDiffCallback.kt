package pro.apir.tko.presentation.ui.main.inventory.edit

import androidx.recyclerview.widget.DiffUtil
import pro.apir.tko.domain.model.ImageModel

/**
 * Created by Антон Сарматин
 * Date: 24.01.2020
 * Project: tko-android
 */
class ContainerEditImagesDiffCallback(
        private val oldList: List<ImageModel>,
        private val newList: List<ImageModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].image == newList[newItemPosition].image
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size


}