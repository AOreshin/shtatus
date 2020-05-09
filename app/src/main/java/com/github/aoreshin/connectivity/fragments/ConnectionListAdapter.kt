package com.github.aoreshin.connectivity.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.aoreshin.connectivity.R
import com.github.aoreshin.connectivity.room.Connection

class ConnectionListAdapter(private val supportFragmentManager: FragmentManager, callback: DiffUtil.ItemCallback<Connection>)
    : ListAdapter<Connection, ConnectionViewHolder>(callback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout, parent, false) as LinearLayout

        linearLayout.apply {
            startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            visibility = View.VISIBLE
        }
        return ConnectionViewHolder(linearLayout)
    }

    override fun submitList(list: List<Connection>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
        val connection = getItem(position)

        with(holder) {
            nameTw.text = connection.description
            urlTw.text = connection.url
            statusTw.text = connection.actualStatusCode
            container.apply {
                isLongClickable = true

                setOnLongClickListener {
                    DeletingDialogFragment().apply {
                        arguments = android.os.Bundle().apply {
                            putInt(
                                DeletingDialogFragment.CONNECTION_ID,
                                connection.id!!
                            )
                        }
                        show(supportFragmentManager, "Deleting")
                    }
                    true
                }
            }
        }
    }
}