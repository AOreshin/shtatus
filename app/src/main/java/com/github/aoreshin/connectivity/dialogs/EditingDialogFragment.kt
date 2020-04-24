package com.github.aoreshin.connectivity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.aoreshin.connectivity.R
import com.github.aoreshin.connectivity.dagger.ConnectivityApplication
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.room.ConnectionDao
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditingDialogFragment: DialogFragment() {
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

            val id = arguments?.getInt(DeletingDialogFragment.CONNECTION_ID)

            bindViews(view)

            connectionDao
                .find(id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    populateData(it)
                }

            val dialog = AlertDialog
                .Builder(it)
                .setView(view)
                .setMessage("Edit connection")
                .setPositiveButton("Apply") { _, _ -> }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (validateInputs()) {
                    insert(id, it.context)
                    dialog.dismiss()
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun populateData(connection: Connection) {
        descriptionEt.setText(connection.description)
        urlEt.setText(connection.url)
    }

    private fun insert(id: Int?, context: Context) {
        val disposable = connectionDao
            .find(id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val connection = Connection(
                    it.id,
                    descriptionEt.text.toString(),
                    urlEt.text.toString()
                )

                Completable
                    .fromAction { connectionDao.insert(connection) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d("","Edited successfully!")
                        Toast.makeText(context, "Edited successfully!", Toast.LENGTH_SHORT).show()
                    }
            }, {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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
}