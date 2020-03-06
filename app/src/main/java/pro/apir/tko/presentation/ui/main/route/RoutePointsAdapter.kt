package pro.apir.tko.presentation.ui.main.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_route_point_completed.view.*
import kotlinx.android.synthetic.main.item_route_point_default.view.*
import kotlinx.android.synthetic.main.item_route_point_pending.view.*
import pro.apir.tko.R
import pro.apir.tko.core.extension.round
import pro.apir.tko.domain.model.RoutePointModel
import pro.apir.tko.domain.model.RouteStateConstants.POINT_TYPE_COMPLETED
import pro.apir.tko.domain.model.RouteStateConstants.POINT_TYPE_DEFAULT
import pro.apir.tko.domain.model.RouteStateConstants.POINT_TYPE_PENDING

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
class RoutePointsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = arrayListOf<RoutePointModel>()

    private var listener: OnRoutePointClickedListener? = null

    interface OnRoutePointClickedListener {

        fun onRoutePointClicked(item: RoutePointModel, pos: Int)

    }

    fun setList(data: List<RoutePointModel>) {
        val diffCallback = RoutePointDiffCallback(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: OnRoutePointClickedListener?) {
        this.listener = listener
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int) = data[position].type ?: POINT_TYPE_DEFAULT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POINT_TYPE_COMPLETED -> {
                CompletedHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route_point_completed, parent, false))
            }
            POINT_TYPE_PENDING -> {
                PendingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route_point_pending, parent, false))
            }
            else -> {
                DefaultHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route_point_default, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DefaultHolder -> {
                holder.bind(data[position], position)
            }
            is CompletedHolder -> {
                holder.bind(data[position], position)
            }
            is PendingHolder -> {
                holder.bind(data[position], position)
            }
        }

        holder.itemView.setOnClickListener {
            listener?.onRoutePointClicked(data[position], position)
        }
    }

    inner class DefaultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout = itemView.frameBadgeDefault
        private val textBadge: TextView = itemView.textBadgeDefault
        private val textName: TextView = itemView.textRouteNameDefault
        private val textInfo: TextView = itemView.textRouteInfoDefault

        fun bind(item: RoutePointModel, pos: Int) {
            val distance = item.distance
            textName.text = when (true) {
                distance != null && distance < 1000 -> {
                    textName.context.getString(R.string.text_route_point_title_meters, item.location, distance.toString())
                }
                distance != null && distance > 1000 -> {
                    val kms =  (distance.toDouble() / 1000).round(1)
                    textName.context.getString(R.string.text_route_point_title_kilometers, item.location, kms.toString())
                }
                else -> {
                    textName.context.getString(R.string.text_route_point_title_null_dist, item.location)
                }
            }

            val pluredCount = textInfo.resources.getQuantityString(
                    R.plurals.plurals_containers,
                    item.containersCount
                            ?: 0, item.containersCount)

            textInfo.text = textInfo.context.getString(R.string.text_route_point_info, pluredCount, item.containersVolume.toString())
            textBadge.text = pos.toString()
        }

    }

    inner class PendingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout = itemView.frameBadgePending
        private val textBadge: TextView = itemView.textBadgePending
        private val textName: TextView = itemView.textRouteNamePending
        private val textInfo: TextView = itemView.textRouteInfoPending

        fun bind(item: RoutePointModel, pos: Int) {
            val distance = item.distance
            textName.text = when (true) {
                distance != null && distance < 1000 -> {
                    textName.context.getString(R.string.text_route_point_title_meters, item.location, distance.toString())
                }
                distance != null && distance > 1000 -> {
                    val kms =  (distance.toDouble() / 1000).round(2)
                    textName.context.getString(R.string.text_route_point_title_kilometers, item.location, kms.toString())
                }
                else -> {
                    textName.context.getString(R.string.text_route_point_title_null_dist, item.location)
                }
            }


            val pluredCount = textInfo.resources.getQuantityString(
                    R.plurals.plurals_containers,
                    item.containersCount
                            ?: 0, item.containersCount)

            textInfo.text = textInfo.context.getString(R.string.text_route_point_info, pluredCount, item.containersVolume.toString())
            textBadge.text = pos.toString()
        }

    }

    inner class CompletedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout = itemView.frameBadgeCompleted
        private val textBadge: TextView = itemView.textBadgeCompleted
        private val textName: TextView = itemView.textRouteNameCompleted
        private val textInfo: TextView = itemView.textRouteInfoCompleted

        fun bind(item: RoutePointModel, pos: Int) {
            val distance = item.distance
            textName.text = when (true) {
                distance != null && distance < 1000 -> {
                    textName.context.getString(R.string.text_route_point_title_meters, item.location, distance.toString())
                }
                distance != null && distance > 1000 -> {
                    val kms =  (distance.toDouble() / 1000).round(2)
                    textName.context.getString(R.string.text_route_point_title_kilometers, item.location, kms.toString())
                }
                else -> {
                    textName.context.getString(R.string.text_route_point_title_null_dist, item.location)
                }
            }

            val pluredCount = textInfo.resources.getQuantityString(
                    R.plurals.plurals_containers,
                    item.containersCount
                            ?: 0, item.containersCount)

            textInfo.text = textInfo.context.getString(R.string.text_route_point_info, pluredCount, item.containersVolume.toString())
            textBadge.text = pos.toString()
        }

    }


}