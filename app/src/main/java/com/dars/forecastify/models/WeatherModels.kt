package com.dars.forecastify.models

import com.google.gson.annotations.SerializedName

/**
 * More info: https://openweathermap.org/current
 *
 * @param coord: City geo location, longitude and latitude
 * @param main: Weather data
 * @param weather: More info Weather condition codes
 * @param visibility: Visibility, meter
 * @param wind: Wind data
 * @param clouds: Cloudiness, %
 * @param dt: Time of data calculation, unix, UTC
 * @param base: Internal parameter
 * @param sys: More info
 * @param timezone: Shift in seconds from UTC
 * @param id: City ID
 * @param name: City name
 * @param cod: Internal parameter
 */
data class WeatherData(
    @SerializedName("coord") val coord: WeatherCoord,
    @SerializedName("main") val main: WeatherMain,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind") val wind: WeatherWind,
    @SerializedName("clouds") val clouds: WeatherClouds,
    @SerializedName("dt") val dt: Int,
    @SerializedName("base") val base: String,
    @SerializedName("sys") val sys: WeatherSys,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("cod") val cod: Int
)

/**
 * @param longitude: City geo location, longitude
 * @param latitude: City geo location, latitude
 */
data class WeatherCoord(
    @SerializedName("lon") val longitude: Double,
    @SerializedName("lat") val latitude: Double
)

/**
 * @param temperature: Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
 * @param feelsLike: Temperature. This temperature parameter accounts for the human perception of weather. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
 * @param temperatureMin: Minimum temperature at the moment of calculation. This is minimal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
 * @param temperatureMax: Maximum temperature at the moment of calculation. This is maximal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
 * @param pressure: Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa
 * @param humidity: Humidity, %
 */
data class WeatherMain(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val temperatureMin: Double,
    @SerializedName("temp_max") val temperatureMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int
)

/**
 * @param id: Weather condition id
 * @param main: Group of weather parameters (Rain, Snow, Extreme etc.)
 * @param description: Weather condition within the group
 * @param icon: Weather icon id
 */
data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

/**
 * @param speed: Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
 * @param deg: Wind direction, degrees (meteorological)
 */
data class WeatherWind(
    @SerializedName("speed") val speed: Double, @SerializedName("deg") val deg: Int
)

/**
 * @param all: Cloudiness, %
 */
data class WeatherClouds(
    @SerializedName("all") val all: Int
)

/**
 * @param type: Internal parameter
 * @param id: Internal parameter
 * @param country: Country code (CR, US, JP etc.)
 * @param sunrise: Sunrise time, unix, UTC
 * @param sunset: Sunset time, unix, UTC
 */
data class WeatherSys(
    @SerializedName("type") val type: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("country") val country: String,
    @SerializedName("sunrise") val sunrise: Int,
    @SerializedName("sunset") val sunset: Int
)
