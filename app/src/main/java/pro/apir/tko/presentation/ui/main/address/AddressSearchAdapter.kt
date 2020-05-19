package pro.apir.tko.presentation.ui.main.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_suggestion.view.*
import pro.apir.tko.R
import pro.apir.tko.domain.model.AddressModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
class AddressSearchAdapter : RecyclerView.Adapter<AddressSearchAdapter.SuggestionHolder>() {

    private val data = arrayListOf<AddressModel>()

    private var listener: AddressSearchAdapter.OnItemClickListener? = null

    interface OnItemClickListener {

        fun onSuggestionSelected(data: AddressModel)

    }

    fun setList(data: List<AddressModel>) {
        val diffCallback = AddressDiffCallback(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        return SuggestionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SuggestionHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class SuggestionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textAddress: TextView
        private val textCoordinates: TextView

        init {
            textAddress = itemView.textAddress
            textCoordinates = itemView.textCoordinates
        }

        fun bind(item: AddressModel) {

            textAddress.text = item.unrestrictedValue

            if (item.lat != null && item.lng != null) {
                textCoordinates.text = textCoordinates.context.getString(R.string.text_coordinates_placeholder, item.lat.toString(), item.lng.toString())
            } else {
                textCoordinates.text = ""
            }

            itemView.setOnClickListener { listener?.onSuggestionSelected(item) }

        }


    }

}