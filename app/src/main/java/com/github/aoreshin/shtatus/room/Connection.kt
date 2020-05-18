package com.github.aoreshin.shtatus.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Connection(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "status") val actualStatusCode: String
) {

    override fun toString(): String {
        return "Connection(id=$id, description='$description', url='$url', actualStatusCode='$actualStatusCode')"
    }
}