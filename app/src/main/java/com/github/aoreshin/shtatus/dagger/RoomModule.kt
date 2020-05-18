package com.github.aoreshin.shtatus.dagger

import android.app.Application
import androidx.room.Room
import com.github.aoreshin.shtatus.room.ConnectionDao
import com.github.aoreshin.shtatus.room.ConnectivityDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {
    private val connectivityDatabase: ConnectivityDatabase = Room
        .databaseBuilder(application, ConnectivityDatabase::class.java, "connectivity-database")
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun database(): ConnectivityDatabase {
        return connectivityDatabase
    }

    @Singleton
    @Provides
    fun dao(): ConnectionDao {
        return connectivityDatabase.connectionDao()
    }
}