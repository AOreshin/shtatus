package com.github.aoreshin.connectivity

import android.app.Application
import com.github.aoreshin.connectivity.dagger.AppModule
import com.github.aoreshin.connectivity.dagger.ApplicationComponent
import com.github.aoreshin.connectivity.dagger.DaggerApplicationComponent
import com.github.aoreshin.connectivity.dagger.RoomModule

class ConnectivityApplication : Application() {
    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .roomModule(RoomModule(this))
            .build()
    }
}
