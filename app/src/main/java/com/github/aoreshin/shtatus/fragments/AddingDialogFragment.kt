package com.github.aoreshin.shtatus.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.github.aoreshin.shtatus.ShatusApplication
import com.github.aoreshin.shtatus.R
import com.github.aoreshin.shtatus.room.Connection
import com.github.aoreshin.shtatus.viewmodels.AddingViewModel
import javax.inject.Inject

class AddingDialogFragment : DialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var descriptionEt: EditText
    private lateinit var urlEt: EditText
    private lateinit var viewModel: AddingViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val application = it.application as ShatusApplication
            application.appComponent.inject(this)

            val view = requireActivity()
                .layoutInflater
                .inflate(R.layout.fragment_add_connection, null)

            bindViews(view)

            val viewModelProvider = ViewModelProvider(this, viewModelFactory)
            viewModel = viewModelProvider.get(AddingViewModel::class.java)

            setupDialog(it, view)
        } ?: throw IllegalStateException(getString(R.string.error_null_activity))
    }

    private fun setupDialog(fragmentActivity: FragmentActivity, view: View): AlertDialog {
        val builder = AlertDialog.Builder(fragmentActivity)

        val dialog = builder
            .setMessage(getString(R.string.title_add_connection))
            .setView(view)
            .setPositiveButton(getString(R.string.button_add)) { _, _ -> }
            .setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
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

    private fun getConnection() : Connection {
        return Connection(null, descriptionEt.text.toString(), urlEt.text.toString(), "")
    }
}
