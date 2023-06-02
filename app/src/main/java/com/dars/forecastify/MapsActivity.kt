package com.dars.forecastify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dars.forecastify.controllers.LocationController
import com.dars.forecastify.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places.createClient
import com.google.android.libraries.places.api.Places.initialize
import com.google.android.libraries.places.api.Places.isInitialized
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var location: LatLng

    private val locationController = LocationController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Servicio de Googe Maps.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Servicio de Google Maps AutocompleteAPI.
        if (!isInitialized()) {
            initialize(this, BuildConfig.MAPS_API_KEY)
        }

        createClient(this) // Inicializamos el cliente de Places.

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Establecemos el tipo de datos que se obtendran de la API.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                mMap.clear()
                location = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                mMap.addMarker(MarkerOptions().position(location))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            }

            override fun onError(status: Status) {
                Log.e("[MapsActivity]", "An error occurred: $status.")
            }
        })
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

    /**
     * Muestra la ubicación actual del usuario.
     */
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

    /**
     * Obtiene la dirección de la ubicación seleccionada
     */
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