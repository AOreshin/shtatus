package com.github.aoreshin.shtatus

import android.app.Application
import com.github.aoreshin.shtatus.dagger.AppModule
import com.github.aoreshin.shtatus.dagger.ApplicationComponent
import com.github.aoreshin.shtatus.dagger.DaggerApplicationComponent
import com.github.aoreshin.shtatus.dagger.RoomModule

class ShatusApplication : Application() {
    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .roomModule(RoomModule(this))
            .build()
    }
}
