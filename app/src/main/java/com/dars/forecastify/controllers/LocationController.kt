package com.dars.forecastify.controllers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng

class LocationController {

    /**
     * Obtiene la ubicación actual del dispositivo.
     * @param context Contexto de la aplicación.
     * @return Ubicación actual del dispositivo.
     * @throws Exception Si no se pudo obtener la ubicación actual.
     */
    fun getCurrentLocation(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val providers = listOf(
            LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER
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

    fun convertLatLngToLocation(userLocation: LatLng): Location {
        val location = Location("")

        location.latitude = userLocation.latitude
        location.longitude = userLocation.longitude

        return location
    }

    fun convertFromCoordinatesToLocation(latitude: Double, longitude: Double): Location {
        val location = Location("")

        location.latitude = latitude
        location.longitude = longitude

        return location
    }
}