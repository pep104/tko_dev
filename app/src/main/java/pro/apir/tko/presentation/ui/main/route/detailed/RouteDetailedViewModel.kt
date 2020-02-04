package pro.apir.tko.presentation.ui.main.route.detailed

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import pro.apir.tko.di.ViewModelAssistedFactory
import pro.apir.tko.presentation.platform.BaseViewModel

/**
 * Created by antonsarmatin
 * Date: 2020-02-05
 * Project: tko-android
 */
class RouteDetailedViewModel @AssistedInject constructor(@Assisted private val handle: SavedStateHandle) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<RouteDetailedViewModel>


}