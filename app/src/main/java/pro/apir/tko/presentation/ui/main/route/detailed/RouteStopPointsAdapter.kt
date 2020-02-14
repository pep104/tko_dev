package pro.apir.tko.presentation.ui.main.route.detailed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_route_point_completed.view.*
import kotlinx.android.synthetic.main.item_route_point_default.view.*
import kotlinx.android.synthetic.main.item_route_point_pending.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.entities.ROUTE_TYPE_COMPLETED
import pro.apir.tko.presentation.entities.ROUTE_TYPE_PENDING
import pro.apir.tko.presentation.entities.RouteStop

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
class RouteStopPointsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = arrayListOf<RouteStop>()

    fun setList(data: List<RouteStop>) {
        //TODO DIFFUTIL
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    //todo listener

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int) = data[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROUTE_TYPE_COMPLETED -> {
                CompletedHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route_point_completed, parent, false))
            }
            ROUTE_TYPE_PENDING -> {
                PendingtHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route_point_pending, parent, false))
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
            is PendingtHolder -> {
                holder.bind(data[position], position)
            }
        }
    }

    inner class DefaultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout = itemView.frameBadgeDefault
        private val textBadge: TextView = itemView.textBadgeDefault
        private val textName: TextView = itemView.textRouteNameDefault
        private val textInfo: TextView = itemView.textRouteInfoDefault

        fun bind(item: RouteStop, pos: Int) {
            textName.text = textName.context.getString(R.string.text_route_stop_title, item.stop.location, "0")

            val pluredCount = textInfo.resources.getQuantityString(
                    R.plurals.plurals_containers,
                    item.stop.containersCount
                            ?: 0, item.stop.containersCount)

            textInfo.text = textInfo.context.getString(R.string.text_route_stop_info, pluredCount, item.stop.containersVolume.toString())
            textBadge.text = pos.toString()
        }

    }

    inner class PendingtHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout = itemView.frameBadgePending
        private val textBadge: TextView = itemView.textBadgePending
        private val textName: TextView = itemView.textRouteNamePending
        private val textInfo: TextView = itemView.textRouteInfoPending

        fun bind(item: RouteStop, pos: Int) {

        }

    }

    inner class CompletedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout = itemView.frameBadgeCompleted
        private val textBadge: TextView = itemView.textBadgeCompleted
        private val textName: TextView = itemView.textRouteNameCompleted
        private val textInfo: TextView = itemView.textRouteInfoCompleted

        fun bind(item: RouteStop, pos: Int) {

        }

    }


}