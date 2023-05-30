package com.dars.forecastify.controllers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.dars.forecastify.BuildConfig
import com.dars.forecastify.models.WeatherData
import com.dars.forecastify.service.WeatherApiService

class WeatherController {

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getWeatherApiService(): WeatherApiService {
        return getRetrofit().create(WeatherApiService::class.java)
    }

    /**
     * Retorna los datos del clima desde el API.
     * @param context Contexto de la aplicación.
     * @return Retorna la información del clima mediante un objeto WeatherData.
     */
    suspend fun getWeatherData(context: Context, mode: String, units: String, lang: String): WeatherData? {
        val weatherApi = getWeatherApiService()

        val apiKey = BuildConfig.API_KEY

        val location = getCurrentPosition(context)
            ?: throw Exception("No se pudo obtener la ubicación actual.")

        val locationLatitude = location.latitude.toString()
        val locationLongitude = location.longitude.toString()

        return try {
            weatherApi.getWeather(
                locationLatitude,
                locationLongitude,
                apiKey,
                mode,
                units,
                lang
            )
        } catch (e: IOException) {
            throw Exception("Problemas en la conexión de red.")
        } catch (e: Exception) {
            throw Exception("Error al obtener datos del clima.")
        }
    }

    /**
     * Obtiene la ubicación actual del dispositivo.
     * @param context Contexto de la aplicación.
     * @return Ubicación actual del dispositivo.
     * @throws Exception Si no se pudo obtener la ubicación actual.
     */
    private fun getCurrentPosition(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )

        var bestLocation: Location? = null

        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                    bestLocation = location
                }
            }
        }

        return bestLocation
    }

    /**
     * Convierte una fecha en formato Unix a un formato legible.
     * @param unix Fecha en formato Unix.
     * @param fullDate Si se desea obtener la fecha completa o solo la hora.
     * @return Fecha en formato legible.
     */
    fun convertUnixDate(unix: Int, fullDate: Boolean): String {
        val date = Date(unix.toLong() * 1000L)
        val format = if (fullDate) "dd/MM/yyyy hh:mm a" else "hh:mm a"
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }
}