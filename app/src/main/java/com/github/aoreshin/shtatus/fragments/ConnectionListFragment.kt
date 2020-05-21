package com.github.aoreshin.shtatus.fragments

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
import com.github.aoreshin.shtatus.R
import com.github.aoreshin.shtatus.ShatusApplication
import com.github.aoreshin.shtatus.viewmodels.ConnectionListViewModel
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

        val application = (requireActivity().application as ShatusApplication)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            refreshLayout.isRefreshing = savedInstanceState.getBoolean(REFRESHING, false)
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            getConnections().observe(viewLifecycleOwner, Observer { viewAdapter.submitList(it) })
            getStopRefreshingEvent().observe(viewLifecycleOwner, Observer { refreshLayout.isRefreshing = false })
            getNoMatchesEvent().observe(viewLifecycleOwner, Observer { showToast(R.string.status_no_matches) })
            getEmptyTableEvent().observe(viewLifecycleOwner, Observer { showToast(R.string.status_no_connections) })
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
            nameEt.setText(getName())
            urlEt.setText(getUrl())
            statusCodeEt.setText(getStatus())
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
        with(viewModel) {
            refreshLayout.setOnRefreshListener { send() }
            nameEt.addTextChangedListener { setName(it.toString()) }
            urlEt.addTextChangedListener { setUrl(it.toString()) }
            statusCodeEt.addTextChangedListener { setStatus(it.toString()) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(REFRESHING, refreshLayout.isRefreshing)
    }

    private fun showToast(resourceId: Int) {
        Toast.makeText(context, getString(resourceId), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        with(viewModel) {
            getNoMatchesEvent().removeObservers(viewLifecycleOwner)
            getEmptyTableEvent().removeObservers(viewLifecycleOwner)
            getStopRefreshingEvent().removeObservers(viewLifecycleOwner)
            getConnections().removeObservers(viewLifecycleOwner)
        }
    }

    companion object {
        private const val REFRESHING = "isRefreshing"
    }
}