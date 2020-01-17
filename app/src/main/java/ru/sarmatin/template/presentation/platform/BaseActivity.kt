package ru.sarmatin.template.presentation.platform

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProviders
import ru.sarmatin.template.presentation.ui.main.GlobalState

//If you would inject VM with more than SavedStateHandle injection, implement @HasDefaultViewModelProviderFactory
abstract class BaseActivity : AppCompatActivity(){

    internal val globalState: GlobalState by viewModels()

}