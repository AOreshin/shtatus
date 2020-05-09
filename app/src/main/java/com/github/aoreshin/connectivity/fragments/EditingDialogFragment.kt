package com.github.aoreshin.connectivity.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.aoreshin.connectivity.ConnectivityApplication
import com.github.aoreshin.connectivity.R
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.viewmodels.EditingViewModel
import javax.inject.Inject

class EditingDialogFragment: DialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var descriptionEt: EditText
    private lateinit var urlEt: EditText
    private lateinit var viewModel: EditingViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val application = it.application as ConnectivityApplication
            application.appComponent.inject(this)

            val view = requireActivity()
                .layoutInflater
                .inflate(R.layout.fragment_add_connection, null)

            bindViews(view)

            val viewModelProvider = ViewModelProvider(this, viewModelFactory)
            viewModel = viewModelProvider.get(EditingViewModel::class.java)

            val id = arguments?.getInt(DeletingDialogFragment.CONNECTION_ID)

            if (id != null) {
                setupDialog(it, id, view)
            } else {
                throw IllegalStateException(getString(R.string.error_empty_connection_id))
            }
        } ?: throw IllegalStateException(getString(R.string.error_null_activity))
    }

    private fun setupDialog(fragmentActivity: FragmentActivity, connectionId: Int, view: View): AlertDialog {
        val dialog = AlertDialog
            .Builder(fragmentActivity)
            .setView(view)
            .setMessage(getString(R.string.title_edit_connection))
            .setPositiveButton(getString(R.string.button_apply)) { _, _ -> }
            .setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
            .create()

        viewModel.find(connectionId).observe(this, Observer(::populateData))

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (validateInputs()) {
                val connection =
                    Connection(connectionId, descriptionEt.text.toString(), urlEt.text.toString(), "")
                viewModel.save(connection)
                dialog.dismiss()
            }
        }

        return dialog
    }

    private fun populateData(connection: Connection) {
        descriptionEt.setText(connection.description)
        urlEt.setText(connection.url)
    }

    private fun bindViews(view: View) {
        descriptionEt = view.findViewById(R.id.add_connection_description_et)
        urlEt = view.findViewById(R.id.add_connection_url_et)
    }

    private fun validateInputs(): Boolean {
        var result = true

        if (descriptionEt.text.isEmpty()) {
            descriptionEt.error = getString(R.string.error_empty_name)
            result = false
        }

        if (urlEt.text.isEmpty()) {
            urlEt.error = getString(R.string.error_empty_url)
            result = false
        } else if (!Patterns.WEB_URL.matcher(urlEt.text).matches())  {
            urlEt.error = getString(R.string.error_invalid_url)
            result = false
        }

        return result
    }
}