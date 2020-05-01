package com.github.aoreshin.connectivity.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.github.aoreshin.connectivity.ConnectivityApplication
import com.github.aoreshin.connectivity.viewmodels.DeletingViewModel
import javax.inject.Inject

class DeletingDialogFragment: DialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: DeletingViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val application = it.application as ConnectivityApplication
            application.appComponent.inject(this)

            val viewModelProvider = ViewModelProvider(this, viewModelFactory)
            viewModel = viewModelProvider.get(DeletingViewModel::class.java)

            val id = arguments?.getInt(CONNECTION_ID)

            if (id != null) {
                setupDialog(it, id)
            } else {
                throw IllegalStateException("ConnectionId cannot be null")
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setupDialog(fragmentActivity: FragmentActivity, connectionId: Int): AlertDialog {
        val dialog = AlertDialog
            .Builder(fragmentActivity)
            .setMessage("Delete this connection?")
            .setNeutralButton("Edit") { _, _ ->
                EditingDialogFragment().apply {
                        arguments = Bundle().apply { putInt(CONNECTION_ID, connectionId) }
                        show(fragmentActivity.supportFragmentManager, "")
                    }
            }
            .setPositiveButton("Delete") { _, _ -> }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            viewModel.delete(connectionId)
            dialog.dismiss()
        }

        return dialog
    }

    companion object {
        const val CONNECTION_ID = "connectionId"
    }
}