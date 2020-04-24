package com.github.aoreshin.connectivity

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.aoreshin.connectivity.dagger.ConnectivityApplication
import com.github.aoreshin.connectivity.dialogs.DeletingDialogFragment
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.room.ConnectionDao
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import java.util.function.Predicate
import javax.inject.Inject


class ConnectionListFragment : Fragment() {
    @Inject
    lateinit var connectionDao: ConnectionDao

    @Inject
    lateinit var retrofitService: RetrofitService

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var tableLayout: TableLayout
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var nameEt: EditText
    private lateinit var urlEt: EditText
    private lateinit var statusCodeEt: EditText
    private val connections = mutableListOf<Connection>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_connection_list, container, false)

        val application = (activity!!.application as ConnectivityApplication)
        application.appComponent.inject(this)

        bindViews(view)
        setupListeners()

//        if (savedInstanceState == null) {
            loadContents()
//        }

        RxJavaPlugins.setErrorHandler {}
        return view
    }

    private fun bindViews(view: View) {
        with(view) {
            tableLayout = findViewById(R.id.table_layout)
            refreshLayout = findViewById(R.id.refresher)
            nameEt = findViewById(R.id.nameEt)
            urlEt = findViewById(R.id.urlEt)
            statusCodeEt = findViewById(R.id.statusCodeEt)
        }
    }

    private fun setupListeners() {
        refreshLayout.setOnRefreshListener { loadContents() }
        nameEt.addTextChangedListener { filter(getPredicate()) }
        urlEt.addTextChangedListener { filter(getPredicate()) }
        statusCodeEt.addTextChangedListener { filter(getPredicate()) }
    }

    private fun getPredicate(): Predicate<Connection> {
        return Predicate { connection ->
            connection.description.contains(nameEt.text, ignoreCase = true)
                    && connection.url.contains(urlEt.text, ignoreCase = true)
                    && connection.actualStatusCode.contains(statusCodeEt.text, ignoreCase = true)
        }
    }

    private fun filter(predicate: Predicate<Connection>) {
        if (!refreshLayout.isRefreshing) {

            clearTable()

            if (connections.isEmpty()) {
                tableLayout.addView(createTextView("Add some connections already!"))
            } else {
                val filtered = connections.filter { connection -> predicate.test(connection) }

                if (filtered.isEmpty()) {
                    tableLayout.addView(createTextView("No matches!"))
                } else {
                    filtered.forEach { createRow(it) }
                }
            }
        }
    }

    private fun loadContents() {
        val disposable = connectionDao
            .all()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { refreshLayout.isRefreshing = true }
            .subscribe { sendRequests(it) }

        compositeDisposable.add(disposable)
    }

    private fun sendRequests(connections: List<Connection>) {
//        if (getSystemService(context!!, ConnectivityManager::class.java)!!.activeNetwork == null) {
//            tableLayout.addView(createTextView("No internet!"))
//            refreshLayout.isRefreshing = false
//        } else {
            val observables = connections
                .map { connection ->
                    retrofitService
                        .get(connection.url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { connection.actualStatusCode = it.message!! }
                        .doOnSuccess { connection.actualStatusCode = it.code().toString() }
                }

            val disposable = Single
                .mergeDelayError(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    with(this.connections) {
                        clear()
                        addAll(connections)
                    }

                    refreshLayout.isRefreshing = false

                    filter(getPredicate())
                }
                .subscribe({}, { Log.d("", it.message!!) })

            compositeDisposable.add(disposable)
//        }
    }

    private fun clearTable() {
        if (tableLayout.size > 0) {
            while (tableLayout.childCount > 0) {
                tableLayout.removeView(tableLayout.getChildAt(tableLayout.childCount - 1))
            }
        }
    }

    private fun createRow(connection: Connection) {
        val tableRow = TableRow(layoutInflater.context)

        val nameView = createTextView(connection.description)
        val urlView = createTextView(connection.url)
        val actualStatusCodeView = createTextView(connection.actualStatusCode)

        tableRow.apply {
            addView(nameView)
            addView(urlView)
            addView(actualStatusCodeView)

            startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));
            visibility = View.VISIBLE;

            isLongClickable = true
            setOnLongClickListener {
                activity?.supportFragmentManager?.let {
                    DeletingDialogFragment()
                        .apply {
                        arguments = Bundle().apply {
                            putInt(DeletingDialogFragment.CONNECTION_ID, connection.id!!)
                        }
                        show(it, "Deleting")
                    }
                }
                true
            }
        }

        tableLayout.addView(tableRow)
    }

    private fun createTextView(text: String?): TextView {
        return TextView(layoutInflater.context)
            .apply {
                setText(text)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = Gravity.CENTER
            }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}
