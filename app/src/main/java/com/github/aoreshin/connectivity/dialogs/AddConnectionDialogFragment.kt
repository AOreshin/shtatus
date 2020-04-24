package com.github.aoreshin.connectivity.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.aoreshin.connectivity.MainActivity
import com.github.aoreshin.connectivity.R
import com.github.aoreshin.connectivity.dagger.ConnectivityApplication
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.room.ConnectionDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddConnectionDialogFragment : DialogFragment() {
    private val compositeDisposable = CompositeDisposable()

    private lateinit var descriptionEt: EditText
    private lateinit var urlEt: EditText

    @Inject
    lateinit var connectionDao: ConnectionDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val application = it.application as ConnectivityApplication

            application.appComponent.inject(this)

            val view = requireActivity()
                .layoutInflater
                .inflate(R.layout.fragment_add_connection, null)

            bindViews(view)

            val builder = AlertDialog.Builder(it)

            val dialog = builder
                .setMessage("Add connection")
                .setView(view)
                .setPositiveButton("Add") { _, _ -> }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (validateInputs()) {
                    addConnection(getConnection())
                    dialog.dismiss()
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun addConnection(connection: Connection) {
        val disposable = Completable
            .fromAction { connectionDao.insert(connection) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(activity, "Connection added successfully!", Toast.LENGTH_LONG).show()
            }, {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            })

        compositeDisposable.add(disposable)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun getConnection() : Connection {
        return Connection(null, descriptionEt.text.toString(), urlEt.text.toString())
    }
}
