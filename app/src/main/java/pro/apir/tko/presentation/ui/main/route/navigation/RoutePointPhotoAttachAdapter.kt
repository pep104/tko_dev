package pro.apir.tko.presentation.ui.main.route.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_route_photo.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.PhotoModel
import pro.apir.tko.domain.model.RouteStateConstants
import pro.apir.tko.presentation.extension.dpToPx
import java.io.File

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
class RoutePointPhotoAttachAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = arrayListOf<ListItem>(ListItem.AddButton)

    private var listener: AttachInteractionListener? = null

    init {
        notifyDataSetChanged()
    }

    interface AttachInteractionListener {

        fun onDeletePhoto(photo: PhotoModel)

        fun onAddNewPhoto()

    }

    fun setPhoto(list: List<PhotoModel>, pointType: Int?) {
        setData(list.map { ListItem.Image(it) }, pointType)
    }

    private fun setData(list: List<ListItem>, pointType: Int?) {
        val newList = list.filter { it is ListItem.Image }.toMutableList()
        if (pointType != RouteStateConstants.POINT_TYPE_COMPLETED)
            newList.add(ListItem.AddButton)
        val diffCallback = PhotoAttachDiffCallback(this.data, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: AttachInteractionListener?) {
        this.listener = listener
    }


    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ListItem.AddButton.type -> {
                AddButtonHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_route_photo_add, parent, false))
            }
            else -> {
                ImageHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_route_photo, parent, false))
            }
        }
    }


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageHolder -> holder.bind(data[position] as ListItem.Image)
            is AddButtonHolder -> holder.bind()
        }
    }

    inner class AddButtonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() {
            itemView.setOnClickListener {
                listener?.onAddNewPhoto()
            }
        }

    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.image
        private val deleteView: ImageView = itemView.delete

        fun bind(image: ListItem.Image) {

            deleteView.isVisible = image.photo.type == PhotoModel.Type.LOCAL

            val glide = when (image.photo.type) {
                PhotoModel.Type.LOCAL -> {
                    Glide.with(imageView)
                        .load(File(image.photo.path))
                }
                PhotoModel.Type.REMOTE -> {
                    Glide.with(imageView)
                        //FIXME url dynamic?
                        .load(imageView.context.getString(R.string.url_file, image.photo.path))
                }
            }

            glide.diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CenterCrop(), RoundedCorners(6.dpToPx))
                .into(imageView)


            deleteView.setOnClickListener {
                listener?.onDeletePhoto(image.photo)
            }

        }

    }


    sealed class ListItem(val type: Int) {
        object AddButton : ListItem(0)
        data class Image(val photo: PhotoModel) : ListItem(1)
    }


}