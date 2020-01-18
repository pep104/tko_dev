package pro.apir.tko.presentation.platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pro.apir.tko.App
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.di.ViewModelFactory
import pro.apir.tko.di.component.AppComponent
import pro.apir.tko.presentation.ui.main.GlobalState
import javax.inject.Inject

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
//TODO LOADING HANDLING
abstract class BaseFragment : Fragment(), HasDefaultViewModelProviderFactory {

    abstract fun layoutId(): Int

    abstract fun handleFailure(): LiveData<Failure>?

    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as App).appComponent
    }

    internal val globalState: GlobalState by activityViewModels()

    private val failureObserver by lazy {
        Observer<Failure> {
            when (it) {
                is Failure.FeatureFailure -> TODO()
                Failure.NetworkConnection -> TODO()
                Failure.ServerError -> TODO()
                Failure.RefreshTokenExpired -> {
                    globalState.setUserState(GlobalState.UserState.TokenExpired)
                }
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory = viewModelFactory.create(this, arguments)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(layoutId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleFailure()?.observe(viewLifecycleOwner, failureObserver)
    }

    open fun onBackPressed() {}

    internal fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

//    internal fun showProgress() = progressStatus(View.VISIBLE)
//
//    internal fun hideProgress() = progressStatus(View.GONE)

//    private fun progressStatus(viewStatus: Int) =
//        with(activity) { if (this is BaseActivity) this.progress.visibility = viewStatus }


}