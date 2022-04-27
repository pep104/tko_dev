package pro.apir.tko.presentation.ui.main.inventory.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.sarmatin.mobble.utils.consumablelivedata.ConsumableLiveData
import ru.sarmatin.mobble.utils.consumablelivedata.ConsumableValue
import ru.sarmatin.mobble.utils.consumablelivedata.postValue

/**
 * Created by antonsarmatin
 * Date: 2020-04-08
 * Project: tko-android
 */
class InventoryEditListSharedViewModel : ViewModel() {

    private val _resultEvent = ConsumableLiveData<EditResultEvent>()
    val resultEvent: LiveData<ConsumableValue<EditResultEvent>>
        get() = _resultEvent

    fun setResult(data: EditResultEvent) {
        _resultEvent.postValue(data)
    }


}