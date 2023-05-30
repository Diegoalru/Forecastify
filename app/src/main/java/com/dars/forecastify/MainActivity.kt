package com.dars.forecastify

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dars.forecastify.controllers.WeatherController
import com.dars.forecastify.models.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val locationPermissionRequestCode = 100

    private lateinit var cityCountry: TextView
    private lateinit var actualDate: TextView
    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var mainDescription: TextView
    private lateinit var mainTemperature: TextView
    private lateinit var feelsLike: TextView
    private lateinit var minTemperature: TextView
    private lateinit var maxTemperature: TextView
    private lateinit var humidity: TextView
    private lateinit var windiness: TextView
    private lateinit var pressure: TextView
    private lateinit var sunrise: TextView
    private lateinit var sunset: TextView

    private lateinit var mainContent: View
    private lateinit var loadingContent: View

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val weatherController = WeatherController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mainContent = findViewById(R.id.main_container)
        loadingContent = findViewById(R.id.loading_container)

        cityCountry = findViewById(R.id.address)
        actualDate = findViewById(R.id.updated_at)
        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
        mainDescription = findViewById(R.id.status)
        mainTemperature = findViewById(R.id.temp)
        feelsLike = findViewById(R.id.feels_like)
        minTemperature = findViewById(R.id.temp_min)
        maxTemperature = findViewById(R.id.temp_max)
        sunrise = findViewById(R.id.sunrise)
        sunset = findViewById(R.id.sunset)
        windiness = findViewById(R.id.wind)
        pressure = findViewById(R.id.pressure)
        humidity = findViewById(R.id.humidity)

        checkLocatePermission()

        // Iniciamos la aplicación con la ubicación actual
        refreshWeather(mainContent)
    }

    private fun checkLocatePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
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

    fun refreshWeather(view: View) {
        coroutineScope.launch {
            changeContent(true)

            try {
                val weatherData = weatherController.getWeatherData(this@MainActivity)

                if (weatherData != null) {
                    updateUI(weatherData)
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener datos.", Toast.LENGTH_SHORT)
                        .show()
                }
                changeContent(false)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_LONG).show()
                // Esperamos 5 segundos para volver a intentar
                delay(5000)

                checkLocatePermission()
                refreshWeather(view)
            }
        }
    }

    private fun updateUI(weatherData: WeatherData) {
        with(weatherData) {
            cityCountry.text = getString(R.string.city_country, name, sys.country)
            actualDate.text =
                getString(R.string.actual_date, weatherController.convertUnixDate(dt, true))
            latitude.text = getString(R.string.latitude, coord.latitude.toString())
            longitude.text = getString(R.string.longitude, coord.longitude.toString())
            mainDescription.text =
                getString(R.string.main_description, weather[0].description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                })
            mainTemperature.text = getString(R.string.main_temperature, main.temperature.toString())
            feelsLike.text = getString(R.string.feels_like, main.feelsLike.toString())
            minTemperature.text =
                getString(R.string.min_temperature, main.temperatureMin.toString())
            maxTemperature.text =
                getString(R.string.max_temperature, main.temperatureMax.toString())
            sunrise.text =
                getString(R.string.sunrise, weatherController.convertUnixDate(sys.sunrise, false))
            sunset.text =
                getString(R.string.sunset, weatherController.convertUnixDate(sys.sunset, false))
            windiness.text = getString(R.string.wind, wind.speed.toString())
            pressure.text = getString(R.string.pressure, main.pressure.toString())
            humidity.text = getString(R.string.humidity, main.humidity.toString())
        }
    }

    private fun changeContent(isLoading: Boolean) {
        if (isLoading) {
            mainContent.visibility = View.GONE
            loadingContent.visibility = View.VISIBLE
        } else {
            mainContent.visibility = View.VISIBLE
            loadingContent.visibility = View.GONE
        }
    }
}
