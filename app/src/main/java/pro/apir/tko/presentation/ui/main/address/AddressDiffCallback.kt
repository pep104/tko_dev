package pro.apir.tko.presentation.ui.main.address

import androidx.recyclerview.widget.DiffUtil
import pro.apir.tko.domain.model.AddressModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
class AddressDiffCallback(
        private val oldList: List<AddressModel>,
        private val newList: List<AddressModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].value == newList[newItemPosition].value) && (oldList[oldItemPosition].unrestrictedValue == newList[newItemPosition].unrestrictedValue)
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size


}