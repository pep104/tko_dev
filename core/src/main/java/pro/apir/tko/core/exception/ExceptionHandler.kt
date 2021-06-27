package pro.apir.tko.core.exception

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Created by antonsarmatin
 * Date: 27/06/2021
 * Project: tko
 */
object ExceptionHandler {

    private val firebase = FirebaseCrashlytics.getInstance()

    fun logNonFatal(e: Exception){
        Log.e("ExceptionHandler","Non fatal: ${e.localizedMessage ?: "NonFatal event without message"}")
        firebase.log(e.localizedMessage ?: "Non fatal event without message")
    }

}

inline fun tryHandled(block: () -> Unit) {
    try {
        block()
    }catch (e: Exception){
        ExceptionHandler.logNonFatal(e)
    }
}