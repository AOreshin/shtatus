package com.github.aoreshin.connectivity.fragments

import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aoreshin.connectivity.R

class ConnectionViewHolder(linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout) {
    val nameTw: TextView = linearLayout.findViewById(R.id.nameTw)
    val urlTw: TextView = linearLayout.findViewById(R.id.urlTw)
    val statusTw: TextView = linearLayout.findViewById(R.id.statusTw)
    val container: LinearLayout = linearLayout
}