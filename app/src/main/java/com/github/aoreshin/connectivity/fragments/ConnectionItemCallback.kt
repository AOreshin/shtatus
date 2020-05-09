package com.github.aoreshin.connectivity.fragments

import androidx.recyclerview.widget.DiffUtil
import com.github.aoreshin.connectivity.room.Connection

class ConnectionItemCallback : DiffUtil.ItemCallback<Connection>() {
    override fun areItemsTheSame(oldItem: Connection, newItem: Connection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Connection, newItem: Connection): Boolean {
        return oldItem.description == newItem.description
                && oldItem.url == newItem.url
                && oldItem.actualStatusCode == newItem.actualStatusCode
    }
}