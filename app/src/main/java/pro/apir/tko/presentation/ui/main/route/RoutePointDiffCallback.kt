package pro.apir.tko.presentation.ui.main.route

import androidx.recyclerview.widget.DiffUtil
import pro.apir.tko.domain.model.RoutePointModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-23
 * Project: tko-android
 */
class RoutePointDiffCallback(
        private val oldList: List<RoutePointModel>,
        private val newList: List<RoutePointModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].id == newList[newItemPosition].id)
                && (oldList[oldItemPosition].location == newList[newItemPosition].location)
                && (oldList[oldItemPosition].type == newList[newItemPosition].type)
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size


}