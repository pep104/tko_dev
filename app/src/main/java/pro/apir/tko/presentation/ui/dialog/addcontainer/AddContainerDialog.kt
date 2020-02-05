package pro.apir.tko.presentation.ui.dialog.addcontainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.shawnlin.numberpicker.NumberPicker
import kotlinx.android.synthetic.main.dialog_add_container.view.*
import pro.apir.tko.R
import pro.apir.tko.presentation.entities.Container
import pro.apir.tko.presentation.extension.getTextValue

/**
 * Created by antonsarmatin
 * Date: 2020-02-03
 * Project: tko-android
 */

class AddContainerDialog(private var listener: AddContainerListener? = null) : DialogFragment() {

    private lateinit var etVolume: EditText
    private lateinit var pickerCount: NumberPicker
    private lateinit var chipGroup: ChipGroup

    interface AddContainerListener {

        fun onNewContainerAdded(list: List<Container>)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.dialog_add_container,
                container,
                false
        );

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etVolume = view.etVolume
        pickerCount = view.number_picker
        chipGroup = view.chipGroup


        view.btnDismiss.setOnClickListener {
            dismissAllowingStateLoss()
        }

        view.btnSubmit.setOnClickListener {
            val volume = etVolume.getTextValue().toDoubleOrNull()
            val count = pickerCount.value
            val type = getSelectedChipValue(chipGroup)
            returnResult(count, type, volume)
            dismissAllowingStateLoss()
        }

    }


    //FIXME create chips programmmticaly from dictionary
    private fun getSelectedChipValue(group: ChipGroup): String {
        for (index in 0 until group.childCount) {
            val chip = group.getChildAt(index) as Chip
            return if (chip.isChecked) {
                when (chip.id) {
                    R.id.chipBunker -> "BUNKER"
                    R.id.chipEuro -> "EURO"
                    R.id.chipSplitted -> "SEPARATE"
                    else -> "STANDARD"
                }
            } else {
                "STANDARD"
            }
        }
        return  "STANDARD"
    }

    private fun returnResult(count: Int, type: String, volume: Double?) {
        val result = mutableListOf<Container>()
        repeat(count) {
            result.add(Container(type, volume))
        }
        listener?.onNewContainerAdded(result)
    }

    override fun onDestroy() {
        listener = null
        super.onDestroy()
    }

}