package pro.apir.tko.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
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

        globalState.menuState.observe(this, Observer {
            val foregroundView = nav_host_fragment.view
            val yTrans = resources.getDimension(R.dimen.activity_menu_height)
            //FIXME CLICK NOT WORKING
            //TODO ObjectAnimator?
//            if (it == true) {
//                val translationAnimation = TranslateAnimation(0f, 0f, 0f, yTrans)
//                with(translationAnimation) {
//                    duration = 300
//                    fillAfter = true
//                    interpolator = AccelerateDecelerateInterpolator()
//                }
//                foregroundView?.startAnimation(translationAnimation)
//            } else {
//                val translationAnimation = TranslateAnimation(0f, 0f, yTrans, 0f)
//                with(translationAnimation) {
//                    duration = 200
//                    fillAfter = true
//                    interpolator = AccelerateDecelerateInterpolator()
//                }
//                foregroundView?.startAnimation(translationAnimation)
//            }

            if(it == true){
                foregroundView?.translationY = yTrans
            }else{
                foregroundView?.translationY = 0f
            }


        })

    }

    private fun setActivityMenuButtons() {
        //Кнопки из верхнего меню-слайдера
        btnActivityMenuClose.setOnClickListener {
            globalState.toggleMenu()
        }
        btnActivityMenuInventory.setOnClickListener {
            navController.navigate(R.id.inventoryListFragment)
            globalState.toggleMenu()
        }
        btnActivityMenuRoutes.setOnClickListener {
            navController.navigate(R.id.routeListFragment)
            globalState.toggleMenu()
        }
    }

    override fun onBackPressed() {
        if (globalState.menuState.value == true) {
            globalState.toggleMenu()
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
