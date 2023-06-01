package com.dars.forecastify

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dars.forecastify.controllers.LocationController
import com.dars.forecastify.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


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

        binding.etSearchLocation.setOnEditorActionListener { text, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchLocation(text.text.toString())
                true
            } else {
                false
            }
        }

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

    private fun searchLocation(locationTxt: String) {
        if (locationTxt.isEmpty()) {
            Toast.makeText(this, "provide location", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val addressList: List<Address>
            val geoCoder = Geocoder(this)
            addressList = geoCoder.getFromLocationName(locationTxt, 1)!!
            mMap.clear()
            location = LatLng(addressList[0].latitude, addressList[0].longitude)
            mMap.addMarker(MarkerOptions().position(location).title(locationTxt))

            // Get current zoom level and add 2
            val zoomLevel = mMap.cameraPosition.zoom + 2f
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
        } catch (e: IOException) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show()
        } catch (e: IndexOutOfBoundsException) {
            Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}