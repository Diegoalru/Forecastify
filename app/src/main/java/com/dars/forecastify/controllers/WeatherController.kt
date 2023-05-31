package com.dars.forecastify.controllers

import android.content.Context
import android.location.Location
import com.dars.forecastify.BuildConfig
import com.dars.forecastify.models.WeatherData
import com.dars.forecastify.service.WeatherApiService
import com.google.android.gms.maps.model.LatLng
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class WeatherController {

    private val locationController = LocationController()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun getWeatherApiService(): WeatherApiService {
        return getRetrofit().create(WeatherApiService::class.java)
    }

    /**
     * Retorna los datos del clima desde el API.
     * @param context Contexto de la aplicación.
     * @return Retorna la información del clima mediante un objeto WeatherData.
     */
    suspend fun getWeatherData(
        context: Context,
        mode: String,
        units: String,
        lang: String,
        userLocation: LatLng?,
    ): WeatherData? {
        val weatherApi = getWeatherApiService()

        val apiKey = BuildConfig.API_KEY

        // convert userLocation to location
        val location: Location? = if (userLocation != null) {
            locationController.convertLatLngToLocation(userLocation)
        } else {
            locationController.getCurrentLocation(context)
        }

        val locationLatitude = location?.latitude.toString()
        val locationLongitude = location?.longitude.toString()

        return try {
            weatherApi.getWeather(
                locationLatitude, locationLongitude, apiKey, mode, units, lang
            )
        } catch (e: IOException) {
            throw Exception("Problemas en la conexión de red.")
        } catch (e: Exception) {
            throw Exception("Error al obtener datos del clima.")
        }
    }
}