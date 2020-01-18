package pro.apir.tko.presentation.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import pro.apir.tko.R
import pro.apir.tko.presentation.platform.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        observeGlobalState()
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

}
