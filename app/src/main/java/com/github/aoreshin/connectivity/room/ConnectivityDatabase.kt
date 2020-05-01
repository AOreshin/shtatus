package com.github.aoreshin.connectivity.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Connection::class], version = 3, exportSchema = false)
abstract class ConnectivityDatabase : RoomDatabase() {
    abstract fun connectionDao(): ConnectionDao
}