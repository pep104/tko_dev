package pro.apir.tko.presentation.entities

import androidx.annotation.StringRes

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */
interface Selector {

    @StringRes
    fun stringId(): Int

    fun key(): String

}