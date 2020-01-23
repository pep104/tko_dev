package pro.apir.tko.data.framework.source

import retrofit2.Retrofit

/**
 * Created by antonsarmatin
 * Date: 2019-12-17
 * Project: android-template
 */
abstract class BaseSource<T> constructor(retrofit: Retrofit) {

//    private val api by lazy { retrofit.create(T::class.java) }

}
