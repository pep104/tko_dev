package pro.apir.tko.presentation.platform

import androidx.lifecycle.ViewModel

/**
 * Created by Антон Сарматин
 * Date: 28.01.2020
 * Project: tko-android
 */
abstract class BaseSharedViewModel : ViewModel(){

    abstract fun consume()

}