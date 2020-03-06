package pro.apir.tko.core.extension

import android.util.Base64
import kotlin.math.round

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */

fun String.fromBase64(): String {
    val bytes = Base64.decode(this.toByteArray(), Base64.DEFAULT)
    return String(bytes)
}

fun Int.roundUpNearest(nearest: Int): Int {
    if (nearest > 0){
        if (this % nearest == 0) return this
        return (nearest - this % nearest) + this
    }else throw IllegalArgumentException("Nearest param must be greater than 0")
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}