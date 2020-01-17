package ru.sarmatin.template.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import ru.sarmatin.template.di.ViewModelAssistedFactory

/**
 * Created by antonsarmatin
 * Date: 2019-12-28
 * Project: android-template
 */
class GlobalState @AssistedInject constructor(@Assisted handle: SavedStateHandle): ViewModel(){

    @AssistedInject.Factory
    interface Factory: ViewModelAssistedFactory<GlobalState>

}