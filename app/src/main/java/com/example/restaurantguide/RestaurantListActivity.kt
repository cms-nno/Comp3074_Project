package com.example.restaurantguide

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.restaurantguide.databinding.ActivityRestaurantListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantListBinding
    private var searchJob: Job? = null

    private val adapter = RestaurantAdapter { item ->
        // The details screen is built in stage 5
        Toast.makeText(this, item.restaurant.name, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarPadding(binding.root)

        binding.rvRestaurants.adapter = adapter
        binding.etSearch.setText(intent.getStringExtra(EXTRA_QUERY).orEmpty())
        binding.etSearch.doAfterTextChanged { loadRestaurants() }
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddEditRestaurantActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadRestaurants()
    }

    private fun loadRestaurants() {
        val query = binding.etSearch.text?.toString()?.trim().orEmpty()
        searchJob?.cancel() // ignore an older search if the user keeps typing
        searchJob = lifecycleScope.launch {
            val results = AppDatabase.get(this@RestaurantListActivity)
                .restaurantDao().search(query)
            adapter.submitList(results)
            binding.tvEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}