package pro.apir.tko.core.extension

import android.util.Base64

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */

fun String.fromBase64(): String{
    val bytes = Base64.decode(this.toByteArray(), Base64.DEFAULT)
    return String(bytes)
}