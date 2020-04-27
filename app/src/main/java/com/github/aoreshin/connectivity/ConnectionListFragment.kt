package com.github.aoreshin.connectivity

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.aoreshin.connectivity.dagger.ConnectivityApplication
import com.github.aoreshin.connectivity.dialogs.DeletingDialogFragment
import com.github.aoreshin.connectivity.room.Connection
import io.reactivex.plugins.RxJavaPlugins
import java.util.function.Predicate
import javax.inject.Inject


class ConnectionListFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var tableLayout: TableLayout
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var nameEt: EditText
    private lateinit var urlEt: EditText
    private lateinit var statusCodeEt: EditText

    private lateinit var connectionsViewModel: ConnectionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_connection_list, container, false)

        val application = (activity!!.application as ConnectivityApplication)
        application.appComponent.inject(this)

        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        connectionsViewModel = viewModelProvider.get(ConnectionsViewModel::class.java)

        bindViews(view)
        setupListeners()

        if (savedInstanceState != null) {
            nameEt.setText(savedInstanceState.getString("nameEt", ""))
            urlEt.setText(savedInstanceState.getString("urlEt", ""))
            statusCodeEt.setText(savedInstanceState.getString("statusCodeEt", ""))
        }

        update()
        RxJavaPlugins.setErrorHandler {}
        return view
    }

    private fun update() {
        connectionsViewModel.getConnections().observe(viewLifecycleOwner, Observer(::updateTable))
    }

    private fun updateTable(connections: List<Connection>?) {
        refreshLayout.isRefreshing = true

        clearTable()

        if (connections!!.isEmpty()) {
            tableLayout.addView(createTextView("Add some connections already!"))
        } else {
            val filtered = connections.filter { connection -> getPredicate().test(connection) }

            if (filtered.isEmpty()) {
                tableLayout.addView(createTextView("No matches!"))
            } else {
                filtered.forEach { createRow(it) }
            }
        }

        refreshLayout.isRefreshing = false
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
        refreshLayout.setOnRefreshListener {
            connectionsViewModel.loadConnections()
        }
        nameEt.addTextChangedListener { update() }
        urlEt.addTextChangedListener { update() }
        statusCodeEt.addTextChangedListener { update() }
    }

    private fun getPredicate(): Predicate<Connection> {
        return Predicate { connection ->
            connection.description.contains(nameEt.text, ignoreCase = true)
                    && connection.url.contains(urlEt.text, ignoreCase = true)
                    && connection.actualStatusCode.contains(statusCodeEt.text, ignoreCase = true)
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("nameEt", nameEt.text.toString())
        outState.putString("urlEt", urlEt.text.toString())
        outState.putString("statusCodeEt", statusCodeEt.text.toString())
    }
}
