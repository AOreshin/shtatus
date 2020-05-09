package com.github.aoreshin.connectivity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.aoreshin.connectivity.ConnectivityApplication
import com.github.aoreshin.connectivity.R
import com.github.aoreshin.connectivity.viewmodels.ConnectionListViewModel
import javax.inject.Inject

class ConnectionListFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var refreshLayout: SwipeRefreshLayout

    private lateinit var nameEt: EditText
    private lateinit var urlEt: EditText
    private lateinit var statusCodeEt: EditText

    private lateinit var viewModel: ConnectionListViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ConnectionListAdapter

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
        setupRecyclerView()
        return view
    }

    private fun setupObservers() {
        with(viewModel) {
            mediatorConnection.observe(viewLifecycleOwner, Observer { viewAdapter.submitList(it) })

            refreshLiveData.observe(viewLifecycleOwner, Observer { status ->
                when (status) {
                    ConnectionListViewModel.RefreshStatus.LOADING -> refreshLayout.isRefreshing = true
                    ConnectionListViewModel.RefreshStatus.READY -> refreshLayout.isRefreshing = false
                    else -> throwException(status.toString())
                }
            })

            statusLiveData.observe(viewLifecycleOwner, Observer { status ->
                when (status) {
                    ConnectionListViewModel.TableStatus.OK -> { }
                    ConnectionListViewModel.TableStatus.NO_MATCHES -> { showToast(R.string.status_no_matches) }
                    ConnectionListViewModel.TableStatus.EMPTY -> { showToast(R.string.status_no_connections) }
                    else -> throwException(status.toString())
                }
            })
        }
    }

    private fun setupRecyclerView() {
        viewAdapter = ConnectionListAdapter(parentFragmentManager, ConnectionItemCallback())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
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
            recyclerView = findViewById(R.id.recycler_view)
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

    private fun throwException(status: String) {
        throw IllegalStateException(getString(R.string.error_no_such_status) + status)
    }

    private fun showToast(resourceId: Int) {
        Toast.makeText(context, getString(resourceId), Toast.LENGTH_SHORT).show()
    }
}