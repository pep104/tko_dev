package pro.apir.tko.presentation.ui.main.list.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.android.synthetic.main.item_route_list.view.*
import pro.apir.tko.R
import pro.apir.tko.data.framework.dict.OptionsDictionariesManager
import pro.apir.tko.domain.model.RouteModel

/**
 * Created by Антон Сарматин
 * Date: 07.02.2020
 * Project: tko-android
 */
class RouteListAdapter(dictionariesManager: OptionsDictionariesManager) : RecyclerView.Adapter<RouteListAdapter.RouteHolder>() {

    private val periodicityOptions = dictionariesManager.getPeriodicityDictionary()

    interface RouteChooseListener {

        fun onRouteChosen(itemID: Int?)

    }

    private var listener: RouteChooseListener? = null

    private val data = arrayListOf<RouteModel>()

    private var choosenID: Int? = null
    private var lastChoosen: RadioButton? = null

    fun setList(data: List<RouteModel>) {
        val diffCallback = RouteDiffCallback(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: RouteChooseListener?) {
        this.listener = listener
    }


    fun setChosen(id: Int?) {
        choosenID = id
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
            textRouteInfo.text = textRouteInfo.context.getString(R.string.text_route_list_info, periodicityOptions[item.periodicity], distance)

            if (item.id == choosenID) {
                radio.isChecked = true
                lastChoosen = radio
            } else {
                radio.isChecked = false
            }

            if(item.hasExistingSession){
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_rounded_blue_16)
                radio.setTextColor(ContextCompat.getColor(radio.context, R.color.white))
                radio.buttonTintList = ContextCompat.getColorStateList(radio.context, R.color.selector_radio_route_in_progress)
                textRouteInfo.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

            }else{
                itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_rounded_white_16)
                radio.setTextColor(ContextCompat.getColor(radio.context, R.color.blueMain))
                radio.buttonTintList = ContextCompat.getColorStateList(radio.context, R.color.selector_radio_default)
                textRouteInfo.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }

            itemView.setOnClickListener{
                radio.performClick()
            }


            radio.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    lastChoosen?.isChecked = false

                    choosenID = item.id
                    lastChoosen = radio

                    listener?.onRouteChosen(item.id)
                }

            }

        }

    }

}