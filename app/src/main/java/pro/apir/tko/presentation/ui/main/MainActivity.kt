package pro.apir.tko.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseActivity
import java.io.File

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        observeGlobalState()
        setActivityMenuButtons()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun observeGlobalState() {
        globalState.userState.observe(this, Observer {
            when (it) {
                GlobalState.UserState.TokenExpired -> {
                    this.recreate()
                }
            }
        })

        globalState.menuState.observe(this, Observer { state ->
            val dimenY = resources.getDimension(R.dimen.activity_menu_height)

            nav_host_fragment.view?.let {

                if (state == true) {
                    layoutActivityMenu.isVisible = state
                    ViewCompat.animate(it)
                            .translationY(dimenY)
                            .setDuration(300)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setStartDelay(35)
                } else {
                    ViewCompat.animate(it)
                            .translationY(0f)
                            .setDuration(200)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .withEndAction {
                                layoutActivityMenu.isVisible = state
                            }
                    it.setOnTouchListener(null)
                }
            }

        })

    }

    private fun setActivityMenuButtons() {
        //Кнопки из верхнего меню-слайдера
        btnActivityMenuClose.setOnClickListener {
            globalState.closeMenu()
        }
        btnActivityMenuInventory.setOnClickListener {
            navController.navigate(R.id.inventoryListFragment)
            globalState.closeMenu()
        }
        btnActivityMenuRoutes.setOnClickListener {
            navController.navigate(R.id.routeListFragment)
            globalState.closeMenu()
        }
    }

    override fun onBackPressed() {
        if (globalState.menuState.value == true) {
            globalState.closeMenu()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }

    }

}
