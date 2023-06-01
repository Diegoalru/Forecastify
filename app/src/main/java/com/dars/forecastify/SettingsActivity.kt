package com.dars.forecastify

import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            var versionPreference: Preference? = null

            try {
                versionPreference = findPreference("version")
                versionPreference?.summary = BuildConfig.VERSION_NAME
            } catch (e: NameNotFoundException) {
                e.printStackTrace()
                versionPreference?.summary = "Unknown"
            } catch (e: Exception) {
                e.printStackTrace()
                versionPreference?.summary = "Unknown"
            }
        }
    }
}