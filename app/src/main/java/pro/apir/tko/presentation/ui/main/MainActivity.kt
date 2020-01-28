package pro.apir.tko.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
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

    private fun observeGlobalState(){
        globalState.userState.observe(this, Observer {
            when(it){
                GlobalState.UserState.TokenExpired -> {
                    this.recreate()
                }
            }
        })
    }

    companion object {

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }

    }

}
