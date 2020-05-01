package com.github.aoreshin.connectivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.aoreshin.connectivity.fragments.AddingDialogFragment
import com.github.aoreshin.connectivity.fragments.ConnectionListFragment


class MainActivity : AppCompatActivity() {
    private val fragment: ConnectionListFragment by lazy { ConnectionListFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.connectivity_toolbar))
        addFragment()
    }

    private fun addFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            AddingDialogFragment().show(supportFragmentManager, "")
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}