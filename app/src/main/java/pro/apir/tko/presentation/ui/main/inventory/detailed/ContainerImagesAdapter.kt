package pro.apir.tko.presentation.ui.main.inventory.detailed

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_container_list, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.imageView
        private val progressBar: ProgressBar = itemView.progressBar

        fun bind(item: ImageModel, pos: Int) {
            progressBar.visibility = View.VISIBLE
            Glide.with(imageView)
                .load(item.url)
                .placeholder(R.drawable.shape_rounded_grey_8)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .transform(CenterCrop(), RoundedCornersTransformation(8.dpToPx, 0))
                .into(imageView)
        }

    }

}