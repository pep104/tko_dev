package pro.apir.tko

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import pro.apir.tko.di.component.AppComponent
import pro.apir.tko.di.component.DaggerAppComponent
import pro.apir.tko.di.module.AppModule
import pro.apir.tko.di.module.FrameworkModule

class App : Application() {

    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerAppComponent.builder()
                .appModule(AppModule(this, this))
                .frameworkModule(FrameworkModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

    }


}