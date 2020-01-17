package ru.sarmatin.template.presentation.ui.main.example

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import ru.sarmatin.template.di.ViewModelAssistedFactory

/**
 * Created by antonsarmatin
 * Date: 2020-01-15
 * Project: android-template
 */
class ExampleStateViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle) : ViewModel(){

    @AssistedInject.Factory
    interface Factory: ViewModelAssistedFactory<ExampleStateViewModel>

    private val _items = handle.getLiveData<List<String>>("items")
    val items: LiveData<List<String>>
        get() = _items

}