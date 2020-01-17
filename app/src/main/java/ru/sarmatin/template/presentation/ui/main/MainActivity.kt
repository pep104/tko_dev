package ru.sarmatin.template.presentation.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.sarmatin.template.R
import ru.sarmatin.template.presentation.platform.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
