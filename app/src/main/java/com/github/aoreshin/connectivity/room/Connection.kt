package com.github.aoreshin.connectivity.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Connection(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "url") val url: String
) {
    @Ignore var actualStatusCode: String = ""
}