package com.github.aoreshin.connectivity.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.aoreshin.connectivity.ConnectivityApplication
import com.github.aoreshin.connectivity.R
import com.github.aoreshin.connectivity.room.Connection
import com.github.aoreshin.connectivity.viewmodels.ConnectionListViewModel
import javax.inject.Inject


class ConnectionListFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var tableLayout: TableLayout
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var nameEt: EditText
    private lateinit var urlEt: EditText
    private lateinit var statusCodeEt: EditText
    private lateinit var viewModel: ConnectionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_connection_list, container, false)

        val application = (activity!!.application as ConnectivityApplication)
        application.appComponent.inject(this)

        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(ConnectionListViewModel::class.java)

        bindViews(view)
        setupObservers()
        setupListeners()
        addFilterValues()
        return view
    }

    private fun setupObservers() {
        with(viewModel) {
            refreshLiveData.observe(viewLifecycleOwner, Observer { status ->
                when (status) {
                    ConnectionListViewModel.RefreshStatus.LOADING -> refreshLayout.isRefreshing =
                        true
                    ConnectionListViewModel.RefreshStatus.READY -> refreshLayout.isRefreshing =
                        false
                    else -> throw IllegalStateException("No such status $status")
                }
            })

            mediatorConnection.observe(viewLifecycleOwner, Observer {})

            statusLiveData.observe(viewLifecycleOwner, Observer { status ->
                when (status) {
                    ConnectionListViewModel.TableStatus.SHOW -> {
                        tableLayout.removeAllViewsInLayout()
                        viewModel.mediatorConnection.value?.forEach { createRow(it) }
                    }
                    ConnectionListViewModel.TableStatus.NO_MATCHES -> {
                        tableLayout.removeAllViewsInLayout()
                        tableLayout.addView(createTextView("No matches!"))
                    }
                    ConnectionListViewModel.TableStatus.EMPTY -> {
                        tableLayout.removeAllViewsInLayout()
                        tableLayout.addView(createTextView("Add some connections already!"))
                    }
                    else -> throw IllegalStateException("No such status $status")
                }
            })
        }
    }

    private fun addFilterValues() {
        with(viewModel) {
            nameEt.setText(nameLiveData.value)
            urlEt.setText(urlLiveData.value)
            statusCodeEt.setText(actualStatusLiveData.value)
        }
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
        with (viewModel) {
            refreshLayout.setOnRefreshListener { send() }
            nameEt.addTextChangedListener { nameLiveData.value = it.toString() }
            urlEt.addTextChangedListener { urlLiveData.value = it.toString() }
            statusCodeEt.addTextChangedListener { actualStatusLiveData.value = it.toString() }
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

//            startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));
//            visibility = View.VISIBLE;

            launchFragmentOnLongClick(connection)
        }

        tableLayout.addView(tableRow)
    }

    private fun TableRow.launchFragmentOnLongClick(connection: Connection) {
        isLongClickable = true

        setOnLongClickListener {
            activity?.supportFragmentManager?.let {
                DeletingDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(DeletingDialogFragment.CONNECTION_ID, connection.id!!)
                    }
                    show(it, "Deleting")
                }
            }
            true
        }
    }

    private fun createTextView(text: String?): TextView {
        return TextView(layoutInflater.context).apply {
                setText(text)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
            }
    }
}
