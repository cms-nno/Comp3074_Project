package com.example.restaurantguide

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurantguide.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private var lat = 0.0
    private var lng = 0.0
    private var restaurantName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarPadding(binding.root)

        lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        lng = intent.getDoubleExtra(EXTRA_LNG, 0.0)
        restaurantName = intent.getStringExtra(EXTRA_NAME).orEmpty()
        title = restaurantName

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnDirections.setOnClickListener { openDirections() }
    }

    override fun onMapReady(map: GoogleMap) {
        val position = LatLng(lat, lng)
        map.addMarker(MarkerOptions().position(position).title(restaurantName))?.showInfoWindow()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        map.uiSettings.isZoomControlsEnabled = true
    }

    // Opens turn-by-turn directions from the user's current location in Google Maps
    private fun openDirections() {
        val googleMaps = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$lat,$lng"))
            .setPackage("com.google.android.apps.maps")
        try {
            startActivity(googleMaps)
        } catch (e: ActivityNotFoundException) {
            // Google Maps app not installed: open the same route in a web browser
            val web = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
            )
            try {
                startActivity(web)
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(this, "No app available to show directions", Toast.LENGTH_LONG).show()
            }
        }
    }

    // The Up arrow acts like Back, so the details screen keeps its restaurant
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}