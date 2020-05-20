package com.github.aoreshin.shtatus

import android.app.Application
import android.util.Log
import com.github.aoreshin.shtatus.dagger.*
import io.reactivex.plugins.RxJavaPlugins

class ShatusApplication : Application() {
    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        RxJavaPlugins.setErrorHandler { Log.d("ErrorNotDelivered", it.message!!) }

        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .roomModule(RoomModule(this))
            .retrofitModule(RetrofitModule(this))
            .build()
    }
}
