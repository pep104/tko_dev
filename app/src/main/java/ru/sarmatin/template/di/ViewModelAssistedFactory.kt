package ru.sarmatin.template.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-15
 * Project: android-template
 */
interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}