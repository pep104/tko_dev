package pro.apir.tko.presentation.ui.main.inventory.detailed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_image_container_list.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.ImageModel
import pro.apir.tko.presentation.extension.dpToPx

/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
class ContainerImagesAdapter : RecyclerView.Adapter<ContainerImagesAdapter.ImageHolder>() {

    private val data = arrayListOf<ImageModel>()

    fun setData(data: List<ImageModel>) {
        this.data.clear()
        this.data.addAll(data)
        notifyItemRangeChanged(0, data.size)
        //todo diffutil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_container_list, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView

        init {
            imageView = itemView.imageView
        }

        fun bind(item: ImageModel, pos: Int) {
            Glide.with(imageView).load(imageView.context.getString(R.string.url_file, item.url)).transform(RoundedCornersTransformation(8.dpToPx, 0)).into(imageView)
        }

    }

}