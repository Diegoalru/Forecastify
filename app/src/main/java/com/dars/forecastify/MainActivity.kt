package com.dars.forecastify

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.dars.forecastify.controllers.WeatherController
import com.dars.forecastify.databinding.ActivityMainBinding
import com.dars.forecastify.models.WeatherData
import com.dars.forecastify.utils.Language
import com.dars.forecastify.utils.Mode
import com.dars.forecastify.utils.Unit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var unit: Unit

    private val locationPermissionRequestCode = 100
    private val weatherController = WeatherController()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        checkLocatePermission()

        // Iniciamos la aplicación con la ubicación actual
        refreshWeather()
    }

    override fun onResume() {
        super.onResume()

        // Registrar el listener de cambios en las preferencias
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val unitValue = sharedPreferences.getString("unit", "metric")
        updatePreferences(unitValue!!)
        refreshWeather()
    }

    override fun onPause() {
        super.onPause()

        // Anular el registro del listener de cambios en las preferencias
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intentSettings = Intent(this, SettingsActivity::class.java)
                startActivity(intentSettings)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "unit") {
            val unitValue = sharedPreferences?.getString("unit", "metric")
            updatePreferences(unitValue!!)
            refreshWeather()
        }
    }

    private fun checkLocatePermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no ha sido concedido, solicitarlo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequestCode
            )
        }
    }

    fun refreshWeatherEvent(view: View) {
        refreshWeather()
    }

    private fun refreshWeather() {
        coroutineScope.launch {
            changeContent(true)

            // Obtener idioma del sistema
            val lang = when (Locale.getDefault().language) {
                "es" -> Language.ES
                "en" -> Language.EN
                else -> Language.EN
            }

            try {
                val weatherData = weatherController.getWeatherData(
                    this@MainActivity, Mode.JSON.value, unit.value, lang.value
                )

                if (weatherData != null) {
                    updateUI(weatherData)
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener datos.", Toast.LENGTH_SHORT)
                        .show()
                }
                changeContent(false)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_LONG).show()
                // Esperamos 5 segundos para volver a intentar
                delay(5000)

                checkLocatePermission()
                refreshWeather()
            }
        }
    }

    private fun updateUI(weatherData: WeatherData) {
        with(weatherData) {

            val unitOfMeasure = when (unit) {
                Unit.STANDARD -> "°K"
                Unit.METRIC -> "°C"
                Unit.IMPERIAL -> "°F"
            }

            binding.apply {
                cityCountry.text = getString(R.string.city_country, name, sys.country)
                updatedAt.text =
                    getString(R.string.actual_date, weatherController.convertUnixDate(dt, true))
                latitude.text = getString(R.string.latitude, coord.latitude.toString())
                longitude.text = getString(R.string.longitude, coord.longitude.toString())
                mainDescription.text =
                    getString(R.string.main_description, weather[0].description.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    })
                temp.text = getString(
                    R.string.main_temperature,
                    "${main.temperature} $unitOfMeasure"
                )
                feelsLike.text =
                    getString(R.string.feels_like, "${main.feelsLike} $unitOfMeasure")
                tempMin.text = getString(
                    R.string.min_temperature,
                    "${main.temperatureMin} $unitOfMeasure"
                )
                tempMax.text = getString(
                    R.string.max_temperature,
                    "${main.temperatureMax} $unitOfMeasure"
                )
                txtSunrise.text = getString(
                    R.string.sunrise,
                    weatherController.convertUnixDate(sys.sunrise, false)
                )
                txtSunset.text =
                    getString(R.string.sunset, weatherController.convertUnixDate(sys.sunset, false))
                txtWind.text = getString(R.string.wind, wind.speed.toString())
                txtPressure.text = getString(R.string.pressure, main.pressure.toString())
                txtHumidity.text = getString(R.string.humidity, main.humidity.toString())
            }
        }
    }

    private fun changeContent(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                mainContainer.visibility = View.GONE
                loadingContainer.visibility = View.VISIBLE
            } else {
                mainContainer.visibility = View.VISIBLE
                loadingContainer.visibility = View.GONE
            }
        }
    }

    private fun updatePreferences(unitValue: String) {
        unit = when (unitValue) {
            "standard" -> Unit.STANDARD
            "metric" -> Unit.METRIC
            "imperial" -> Unit.IMPERIAL
            else -> Unit.METRIC
        }
    }
}
