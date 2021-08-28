package pro.apir.tko.presentation.platform

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pro.apir.tko.App
import pro.apir.tko.di.ViewModelFactory
import pro.apir.tko.di.component.AppComponent
import pro.apir.tko.presentation.ui.main.GlobalState
import javax.inject.Inject

//If you would inject VM with more than SavedStateHandle injection, implement @HasDefaultViewModelProviderFactory
abstract class BaseActivity : AppCompatActivity(){

    internal val globalState: GlobalState by viewModels()

    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as App).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory = viewModelFactory.create(this)

}