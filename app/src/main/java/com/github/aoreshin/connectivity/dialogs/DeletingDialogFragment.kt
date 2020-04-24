package com.github.aoreshin.connectivity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.aoreshin.connectivity.dagger.ConnectivityApplication
import com.github.aoreshin.connectivity.room.ConnectionDao
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DeletingDialogFragment: DialogFragment() {
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var connectionDao: ConnectionDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val application = it.application as ConnectivityApplication

            application.appComponent.inject(this)

            val id = arguments?.getInt(CONNECTION_ID)

            val dialog = AlertDialog
                .Builder(it)
                .setMessage("Delete this connection?")
                .setNeutralButton("Edit") { _, _ ->
                    EditingDialogFragment()
                        .apply {
                            arguments = Bundle().apply {
                                putInt(CONNECTION_ID, id!!)
                            }
                            show(it.supportFragmentManager, "")
                        }
                }
                .setPositiveButton("Delete") { _, _ -> }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                delete(id, it.context)
                dialog.dismiss()
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun delete(id: Int?, context: Context) {
        val disposable = connectionDao
            .find(id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Completable
                    .fromAction { connectionDao.delete(it) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d("","Deleted successfully!")
                        Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_LONG).show()
                    }
            }, {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            })

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val CONNECTION_ID = "connectionId"
    }
}