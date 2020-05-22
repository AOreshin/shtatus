package com.github.aoreshin.shtatus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        addFragment()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun addFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            with(preferenceManager) {
                findPreference<SwitchPreferenceCompat>(HTTPS_KEY)?.setOnPreferenceChangeListener { preference, newValue ->
                    Log.d(TAG, "Setting $HTTPS_KEY $newValue")

                    preference.sharedPreferences
                        .edit()
                        .putBoolean(HTTPS_KEY, newValue as Boolean)
                        .commit()
                }


                findPreference<SeekBarPreference>(TIMEOUT_KEY)?.setOnPreferenceChangeListener { preference, newValue ->
                        Log.d(TAG, "Setting $TIMEOUT_KEY $newValue")

                        preference.sharedPreferences
                            .edit()
                            .putInt(TIMEOUT_KEY, newValue as Int)
                            .commit()
                    }
            }

            return super.onCreateView(inflater, container, savedInstanceState)
        }

        companion object {
            private const val TAG = "SettingsFragment"
            const val HTTPS_KEY = "enableHttps"
            const val TIMEOUT_KEY = "timeout"
        }
    }
}