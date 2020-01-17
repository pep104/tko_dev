package pro.apir.tko.presentation.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by antonsarmatin
 * Date: 2019-12-17
 * Project: android-template
 */

val Context.networkInfo: NetworkInfo? get() =
    (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo