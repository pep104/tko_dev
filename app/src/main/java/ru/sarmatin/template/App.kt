package ru.sarmatin.template

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.sarmatin.template.di.component.AppComponent
import ru.sarmatin.template.di.component.DaggerAppComponent
import ru.sarmatin.template.di.module.AppModule

class App : Application() {

    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

    }


}