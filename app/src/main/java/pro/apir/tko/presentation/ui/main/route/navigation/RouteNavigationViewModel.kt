package pro.apir.tko.presentation.ui.main.route.navigation

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by Антон Сарматин
 * Date: 10.02.2020
 * Project: tko-android
 *
 */
class RouteNavigationViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle): BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<RouteNavigationViewModel>

}