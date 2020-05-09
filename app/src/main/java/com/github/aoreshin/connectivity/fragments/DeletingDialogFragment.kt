package com.github.aoreshin.connectivity.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.github.aoreshin.connectivity.ConnectivityApplication
import com.github.aoreshin.connectivity.R
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
                throw IllegalStateException(getString(R.string.error_empty_connection_id))
            }
        } ?: throw IllegalStateException(getString(R.string.error_null_activity))
    }

    private fun setupDialog(fragmentActivity: FragmentActivity, connectionId: Int): AlertDialog {
        val dialog = AlertDialog
            .Builder(fragmentActivity)
            .setMessage(getString(R.string.title_delete_connection))
            .setNeutralButton(getString(R.string.button_edit)) { _, _ ->
                EditingDialogFragment().apply {
                        arguments = Bundle().apply { putInt(CONNECTION_ID, connectionId) }
                        show(fragmentActivity.supportFragmentManager, "")
                    }
            }
            .setPositiveButton(getString(R.string.button_delete)) { _, _ -> }
            .setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
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