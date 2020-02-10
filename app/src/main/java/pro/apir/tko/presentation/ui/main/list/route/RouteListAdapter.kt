package pro.apir.tko.presentation.ui.main.list.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.android.synthetic.main.item_route_list.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.RouteModel
import pro.apir.tko.presentation.dict.OptionsDictionariesManager

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
class RouteListAdapter(dictionariesManager: OptionsDictionariesManager) : RecyclerView.Adapter<RouteListAdapter.RouteHolder>() {

    private val peridicityOptions = dictionariesManager.getPeriodicityDictionary()

    interface RouteChooseListener {

        fun onRouteChosen(item: RouteModel)

    }

    private var listener: RouteChooseListener? = null

    private val data = arrayListOf<RouteModel>()

    fun setList(data: List<RouteModel>) {
        val diffCallback = RouteDiffCallback(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(litener: RouteChooseListener?) {
        this.listener = listener
    }

    //todo
    fun setChosen(){

    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder {
        return RouteHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route_list, parent, false))
    }

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class RouteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val radio: MaterialRadioButton = itemView.radio
        private val textRouteInfo: TextView = itemView.textRouteInfo

        fun bind(item: RouteModel, position: Int) {

            val distance = (item.distance.toDouble() / 1000).toString()

            radio.text = item.name
            textRouteInfo.text = textRouteInfo.context.getString(R.string.text_route_list_info, peridicityOptions[item.periodicity], distance)

            //TODO SET choosen


        }

    }

}