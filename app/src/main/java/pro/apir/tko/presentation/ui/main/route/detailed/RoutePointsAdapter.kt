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
import pro.apir.tko.presentation.entities.RoutePoint

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
class RoutePointsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = arrayListOf<RoutePoint>()

    //todo methods

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

        private val frameBadge: FrameLayout
        private val textBadge: TextView
        private val textName: TextView
        private val textInfo: TextView

        init {
            frameBadge = itemView.frameBadgeDefault
            textBadge = itemView.textBadgeDefault
            textName = itemView.textRouteNameDefault
            textInfo = itemView.textRouteInfoDefault
        }

        fun bind(item: RoutePoint, pos: Int) {

        }

    }

    inner class PendingtHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout
        private val textBadge: TextView
        private val textName: TextView
        private val textInfo: TextView

        init {
            frameBadge = itemView.frameBadgePending
            textBadge = itemView.textBadgePending
            textName = itemView.textRouteNamePending
            textInfo = itemView.textRouteInfoPending
        }

        fun bind(item: RoutePoint, pos: Int) {

        }

    }

    inner class CompletedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val frameBadge: FrameLayout
        private val textBadge: TextView
        private val textName: TextView
        private val textInfo: TextView

        init {
            frameBadge = itemView.frameBadgeCompleted
            textBadge = itemView.textBadgeCompleted
            textName = itemView.textRouteNameCompleted
            textInfo = itemView.textRouteInfoCompleted
        }

        fun bind(item: RoutePoint, pos: Int) {

        }

    }


}