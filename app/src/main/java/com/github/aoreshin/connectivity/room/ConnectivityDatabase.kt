package com.github.aoreshin.connectivity.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.room.ConnectionDao

@Database(entities = [Connection::class], version = 2, exportSchema = false)
abstract class ConnectivityDatabase : RoomDatabase() {
    abstract fun connectionDao(): ConnectionDao
}