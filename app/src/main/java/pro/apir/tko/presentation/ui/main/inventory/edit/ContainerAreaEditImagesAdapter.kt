package pro.apir.tko.presentation.ui.main.inventory.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_image_container_edit_list.view.*
import kotlinx.android.synthetic.main.item_image_container_list.view.imageView
import pro.apir.tko.R
import pro.apir.tko.domain.model.ImageModel
import pro.apir.tko.presentation.entities.PhotoWrapper
import pro.apir.tko.presentation.extension.dpToPx
import pro.apir.tko.presentation.ui.main.inventory.edit.diff.ContainerEditImagesDiffCallback

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class ContainerAreaEditImagesAdapter : RecyclerView.Adapter<ContainerAreaEditImagesAdapter.ImageHolder>() {

    private val data = arrayListOf<PhotoWrapper>()

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {

        fun onImageDeleteClicked(item: PhotoWrapper)

    }

    fun setData(data: List<PhotoWrapper>) {
        val diffCallback = ContainerEditImagesDiffCallback(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setDataFromModel(data: List<ImageModel>){
        val list = data.map { PhotoWrapper(it) }
        setData(list)
    }


    fun setListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_container_edit_list, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView
        private val btnDelete: ImageView

        init {
            imageView = itemView.imageView
            btnDelete = itemView.btnDelete
        }

        fun bind(item: PhotoWrapper, pos: Int) {
            if (item.uploaded != null){
                Glide.with(imageView)
                        .load(imageView.context.getString(R.string.url_file, item.uploaded.url))
                        .transform(CenterCrop(), RoundedCornersTransformation(8.dpToPx, 0))
                        .into(imageView)
            }else if(item.new != null){
                Glide.with(imageView)
                        .load(item.new)
                        .transform(CenterCrop(), RoundedCornersTransformation(8.dpToPx, 0))
                        .into(imageView)
            }

            btnDelete.setOnClickListener {
                listener?.onImageDeleteClicked(item)
            }
        }

    }


}