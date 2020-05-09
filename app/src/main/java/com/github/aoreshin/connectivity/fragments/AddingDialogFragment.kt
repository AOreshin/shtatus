package com.github.aoreshin.connectivity.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.github.aoreshin.connectivity.ConnectivityApplication
import com.github.aoreshin.connectivity.R
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.viewmodels.AddingViewModel
import javax.inject.Inject

class AddingDialogFragment : DialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var descriptionEt: EditText
    private lateinit var urlEt: EditText
    private lateinit var viewModel: AddingViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val application = it.application as ConnectivityApplication
            application.appComponent.inject(this)

            val view = requireActivity()
                .layoutInflater
                .inflate(R.layout.fragment_add_connection, null)

            bindViews(view)

            val viewModelProvider = ViewModelProvider(this, viewModelFactory)
            viewModel = viewModelProvider.get(AddingViewModel::class.java)

            setupDialog(it, view)
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setupDialog(fragmentActivity: FragmentActivity, view: View): AlertDialog {
        val builder = AlertDialog.Builder(fragmentActivity)

        val dialog = builder
            .setMessage("Add connection")
            .setView(view)
            .setPositiveButton("Add") { _, _ -> }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (validateInputs()) {
                viewModel.save(getConnection())
                dialog.dismiss()
            }
        }

        return dialog
    }

    private fun bindViews(view: View) {
        descriptionEt = view.findViewById(R.id.add_connection_description_et)
        urlEt = view.findViewById(R.id.add_connection_url_et)
    }

    private fun validateInputs(): Boolean {
        var result = true

        if (descriptionEt.text.isEmpty()) {
            descriptionEt.error = "Description should not be empty"
            result = false
        }

        if (urlEt.text.isEmpty()) {
            urlEt.error = "URL should not be empty"
            result = false
        } else if (!Patterns.WEB_URL.matcher(urlEt.text).matches())  {
            urlEt.error = "URL is not valid"
            result = false
        }

        return result
    }

    private fun getConnection() : Connection {
        return Connection(null, descriptionEt.text.toString(), urlEt.text.toString(), "")
    }
}
