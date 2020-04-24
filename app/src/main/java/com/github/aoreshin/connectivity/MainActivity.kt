package com.github.aoreshin.connectivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.github.aoreshin.connectivity.dialogs.AddConnectionDialogFragment
import com.github.aoreshin.connectivity.room.Connection
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_connection_list.*
import java.util.concurrent.TimeUnit
import java.util.function.Predicate


class MainActivity : AppCompatActivity() {
    private lateinit var fragment: ConnectionListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.connectivity_toolbar))
        addFragment()
    }

    private fun addFragment() {
        fragment = ConnectionListFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            AddConnectionDialogFragment().show(supportFragmentManager, "")
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}