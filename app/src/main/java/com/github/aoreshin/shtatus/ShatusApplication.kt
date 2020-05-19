package com.github.aoreshin.shtatus

import android.app.Application
import com.github.aoreshin.shtatus.dagger.*

class ShatusApplication : Application() {
    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .roomModule(RoomModule(this))
            .retrofitModule(RetrofitModule(this))
            .build()
    }
}
