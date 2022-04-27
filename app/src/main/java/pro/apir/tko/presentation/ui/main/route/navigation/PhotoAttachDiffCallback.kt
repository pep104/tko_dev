package pro.apir.tko.presentation.ui.main.route.navigation

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
class PhotoAttachDiffCallback(
        private val oldList: List<RoutePointPhotoAttachAdapter.ListItem>,
        private val newList: List<RoutePointPhotoAttachAdapter.ListItem>
) : DiffUtil.Callback() {


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return if (old is RoutePointPhotoAttachAdapter.ListItem.Image && new is RoutePointPhotoAttachAdapter.ListItem.Image) {
            //todo compare loc and remote photos
            old.photo.path == new.photo.path && old.photo.type == new.photo.type
        } else {
            old is RoutePointPhotoAttachAdapter.ListItem.AddButton && new is RoutePointPhotoAttachAdapter.ListItem.AddButton
        }

    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

}
