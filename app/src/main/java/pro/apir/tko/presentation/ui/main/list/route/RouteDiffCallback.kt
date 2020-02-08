package pro.apir.tko.presentation.ui.main.list.route

import androidx.recyclerview.widget.DiffUtil
import pro.apir.tko.domain.model.RouteModel

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
class RouteDiffCallback(
        private val oldList: List<RouteModel>,
        private val newList: List<RouteModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].id == newList[newItemPosition].id) && (oldList[oldItemPosition].name == newList[newItemPosition].name)
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

}