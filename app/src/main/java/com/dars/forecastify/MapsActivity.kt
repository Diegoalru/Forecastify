package com.dars.forecastify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dars.forecastify.controllers.LocationController
import com.dars.forecastify.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var location: LatLng

    private val locationController = LocationController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = false

        // Add a marker in the current location and move the camera
        getCurrentLocation()

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()

            // Establecemos la ubicación seleccionada
            location = latLng

            mMap.addMarker(MarkerOptions().position(latLng))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
        }
    }

    fun onGetLocationClick(view: View) {
        mMap.clear()
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        val currentLocation = locationController.getCurrentLocation(this@MapsActivity)
        if (currentLocation != null) {
            // Establecemos la ubicación seleccionada
            location = LatLng(currentLocation.latitude, currentLocation.longitude)

            mMap.addMarker(MarkerOptions().position(location))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        }
    }

    fun onSetLocationClick(view: View) {
        val resultIntent = Intent(this, MainActivity::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            resultIntent.putExtra("latitude", location.latitude)
            resultIntent.putExtra("longitude", location.longitude)
        } else {
            resultIntent.putExtra("location", location)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}