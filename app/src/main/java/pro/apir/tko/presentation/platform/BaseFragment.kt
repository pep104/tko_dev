package pro.apir.tko.presentation.platform

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pro.apir.tko.R
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.exception.TokenUncaughtException
import pro.apir.tko.presentation.extension.alert
import pro.apir.tko.presentation.ui.main.GlobalState


/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
@AndroidEntryPoint
abstract class BaseFragment : Fragment(), HasDefaultViewModelProviderFactory {

    abstract fun layoutId(): Int

    abstract fun handleFailure(): LiveData<Failure>?



    internal val globalState: GlobalState by activityViewModels()

    internal open val failureObserver by lazy {
        Observer<Failure> {
            when (it) {
                is Failure.FeatureFailure -> throw NotImplementedError("You should override failureObserver to handle FeatureFailure - ${it.javaClass.simpleName}")
                Failure.NetworkConnection -> alert(R.string.error_network_connection)
                is Failure.ServerError -> {
                    if (it.errorMessage != null) {
                        alert(it.errorMessage)
                    }
                }
                is Failure.RefreshTokenNotValid -> {
                    if(!it.isExpired)
                        FirebaseCrashlytics.getInstance().recordException(
                            TokenUncaughtException()
                        )
                    globalState.setUserState(GlobalState.UserState.TokenExpired)
                }
            }
        }
    }
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelFactory

//    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory = viewModelFactory.create(this, arguments)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
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

    internal fun back(view: View) {
        activity?.onBackPressed()
    }

    internal fun setStatusBarColor(@ColorRes color: Int) {
        val startColor = activity?.window?.statusBarColor
        val endColor = ContextCompat.getColor(requireContext(), color)
        ObjectAnimator.ofArgb(activity?.window, "statusBarColor", startColor!!, endColor).start()
    }

    internal fun setStatusBarLightMode(isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility =
                if (isLight) View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

    }

    fun MyLocationNewOverlay.setCustomLocationMarkers() {
        val point = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_map_static
        )?.toBitmap()
        val arrow = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_map_arrow
        )?.toBitmap()

        this.setDirectionArrow(
            point,
            arrow
        )

        if (point != null)
            this.setPersonHotspot(point.width.toFloat() / 2f,
                point.height.toFloat() / 2f)
    }

}