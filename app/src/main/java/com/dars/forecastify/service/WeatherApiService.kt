package com.dars.forecastify.service

import com.dars.forecastify.models.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") location_latitude: String,
        @Query("lon") location_longitude: String,
        @Query("appid") apiKey: String,
        @Query("mode") mode: String,
        @Query("units") units: String,
        @Query("lang") language: String,
    ): WeatherData

}