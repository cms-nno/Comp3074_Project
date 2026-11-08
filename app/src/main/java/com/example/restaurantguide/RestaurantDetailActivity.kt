package com.example.restaurantguide

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.restaurantguide.databinding.ActivityRestaurantDetailBinding
import kotlinx.coroutines.launch

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDetailBinding
    private var restaurantId = -1L
    private var current: Restaurant? = null

    private val reviewAdapter = ReviewAdapter { row -> confirmDeleteReview(row) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarPadding(binding.root)

        restaurantId = intent.getLongExtra(EXTRA_RESTAURANT_ID, -1L)
        if (restaurantId == -1L) {
            finish()
            return
        }

        binding.rvReviews.adapter = reviewAdapter
        binding.btnMap.setOnClickListener { openMap() }
        binding.btnEdit.setOnClickListener {
            startActivity(
                Intent(this, AddEditRestaurantActivity::class.java)
                    .putExtra(EXTRA_RESTAURANT_ID, restaurantId)
            )
        }
        binding.btnDelete.setOnClickListener { confirmDeleteRestaurant() }
        binding.btnSubmitReview.setOnClickListener { submitReview() }
    }

    // Reload every time we come back (for example after editing)
    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        lifecycleScope.launch {
            val db = AppDatabase.get(this@RestaurantDetailActivity)
            val item = db.restaurantDao().getWithRating(restaurantId)
            if (item == null) { // the restaurant was deleted
                finish()
                return@launch
            }
            val r = item.restaurant
            current = r
            binding.tvName.text = r.name
            binding.tvRating.text = ratingLabel(item.avgRating, item.reviewCount)
            binding.tvTags.text = tagsLabel(r.tags)
            binding.tvAddress.text = r.address
            binding.tvDescription.text = r.description

            val reviews = db.reviewDao().forRestaurant(restaurantId)
            reviewAdapter.submitList(reviews.map { it.toRow() })
            binding.tvNoReviews.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun openMap() {
        val r = current ?: return
        val lat = r.latitude
        val lng = r.longitude
        if (lat == null || lng == null) {
            Toast.makeText(
                this,
                "No map location for this restaurant. Edit it and check the address.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        startActivity(
            Intent(this, MapActivity::class.java)
                .putExtra(EXTRA_LAT, lat)
                .putExtra(EXTRA_LNG, lng)
                .putExtra(EXTRA_NAME, r.name)
        )
    }

    private fun submitReview() {
        val rating = binding.ratingBar.rating.toInt()
        if (rating < 1) {
            Toast.makeText(this, "Please choose a star rating", Toast.LENGTH_SHORT).show()
            return
        }
        val comment = binding.etComment.text.toString().trim()
        lifecycleScope.launch {
            AppDatabase.get(this@RestaurantDetailActivity).reviewDao().insert(
                Review(
                    restaurantId = restaurantId,
                    rating = rating,
                    comment = comment,
                    visitDate = System.currentTimeMillis()
                )
            )
            binding.ratingBar.rating = 0f
            binding.etComment.text?.clear()
            load()
        }
    }

    private fun confirmDeleteReview(row: ReviewRow) {
        AlertDialog.Builder(this)
            .setTitle("Delete this review?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    AppDatabase.get(this@RestaurantDetailActivity).reviewDao().deleteById(row.id)
                    load()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDeleteRestaurant() {
        val r = current ?: return
        AlertDialog.Builder(this)
            .setTitle("Delete ${r.name}?")
            .setMessage("This also deletes all of its reviews.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    AppDatabase.get(this@RestaurantDetailActivity).restaurantDao().delete(r)
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}