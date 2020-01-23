package pro.apir.tko.presentation.ui.main.address

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pro.apir.tko.domain.model.SuggestionModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
class SuggestionsAdapter : RecyclerView.Adapter<SuggestionsAdapter.SuggestionHolder>() {

    private val data = arrayListOf<SuggestionModel>()

    private var listener: SuggestionsAdapter.OnItemClickListener? = null

    interface OnItemClickListener {

        fun onAddressItemChoosed(data: SuggestionModel)

    }

    fun setList(data: List<SuggestionModel>){
        val diffCallback = SuggestionDiffCallback(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SuggestionHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class SuggestionHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        //todo views

        fun bind(item: SuggestionModel){

        }


    }

}