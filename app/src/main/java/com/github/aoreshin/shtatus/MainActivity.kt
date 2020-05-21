package com.github.aoreshin.shtatus

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.aoreshin.shtatus.fragments.AddingDialogFragment
import com.github.aoreshin.shtatus.fragments.ConnectionListFragment


class MainActivity : AppCompatActivity() {
    private lateinit var fragment: ConnectionListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.connectivity_toolbar))
        addFragment(savedInstanceState)
    }

    private fun addFragment(savedInstanceState: Bundle?) {
        fragment = if (savedInstanceState != null) {
            supportFragmentManager.getFragment(savedInstanceState, KEY) as ConnectionListFragment
        } else {
            ConnectionListFragment()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            AddingDialogFragment().show(supportFragmentManager, "")
            true
        }
        R.id.action_settings -> {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, KEY, fragment)
    }

    companion object {
        private const val KEY = "listFragment"
    }
}