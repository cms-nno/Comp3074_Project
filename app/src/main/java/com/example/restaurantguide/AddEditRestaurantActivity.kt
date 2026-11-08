package com.example.restaurantguide

import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.restaurantguide.databinding.ActivityAddEditRestaurantBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class AddEditRestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditRestaurantBinding
    private var editingId: Long = -1L        // -1 means we are adding a new restaurant
    private var existing: Restaurant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarPadding(binding.root)

        editingId = intent.getLongExtra(EXTRA_RESTAURANT_ID, -1L)
        if (editingId != -1L) {
            title = "Edit Restaurant"
            lifecycleScope.launch {
                val r = AppDatabase.get(this@AddEditRestaurantActivity)
                    .restaurantDao().getById(editingId)
                if (r != null) {
                    existing = r
                    binding.etName.setText(r.name)
                    binding.etAddress.setText(r.address)
                    binding.etDescription.setText(r.description)
                    binding.etTags.setText(r.tags.replace(",", ", "))
                }
            }
        }

        binding.btnSave.setOnClickListener { save() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun save() {
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val tags = binding.etTags.text.toString()
            .split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
            .joinToString(",")

        // Validation
        binding.tilName.error = if (name.isEmpty()) "Name is required" else null
        binding.tilAddress.error = if (address.isEmpty()) "Address is required" else null
        if (name.isEmpty() || address.isEmpty()) return

        // In edit mode, wait until the restaurant has finished loading
        if (editingId != -1L && existing == null) return

        binding.btnSave.isEnabled = false
        lifecycleScope.launch {
            val dao = AppDatabase.get(this@AddEditRestaurantActivity).restaurantDao()
            val old = existing

            // Only look up coordinates when they are missing or the address changed
            var lat = old?.latitude
            var lng = old?.longitude
            if (old == null || old.address != address || lat == null || lng == null) {
                val coords = geocode(address)
                lat = coords?.first
                lng = coords?.second
            }

            if (old == null) {
                dao.insert(
                    Restaurant(
                        name = name, address = address, description = description,
                        tags = tags, latitude = lat, longitude = lng
                    )
                )
            } else {
                dao.update(
                    old.copy(
                        name = name, address = address, description = description,
                        tags = tags, latitude = lat, longitude = lng
                    )
                )
            }

            val message = if (lat == null)
                "Saved, but the address could not be found on the map. Check the address."
            else "Restaurant saved"
            Toast.makeText(this@AddEditRestaurantActivity, message, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // Turns an address into (latitude, longitude) using Android's built-in Geocoder
    @Suppress("DEPRECATION")
    private suspend fun geocode(address: String): Pair<Double, Double>? =
        withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) return@withContext null
                val results = Geocoder(this@AddEditRestaurantActivity, Locale.getDefault())
                    .getFromLocationName(address, 1)
                val first = results?.firstOrNull()
                if (first != null) Pair(first.latitude, first.longitude) else null
            } catch (e: IOException) {
                null // no internet or the geocoder service is unavailable
            }
        }
}