package com.example.restaurantguide

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.restaurantguide.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

// This is the Home screen
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val topAdapter = RestaurantAdapter { item ->
        startActivity(
            Intent(this, RestaurantDetailActivity::class.java)
                .putExtra(EXTRA_RESTAURANT_ID, item.restaurant.id)
        )
    }
    private val recentAdapter = ReviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarPadding(binding.root)

        binding.rvTop.adapter = topAdapter
        binding.rvRecent.adapter = recentAdapter

        binding.btnSearch.setOnClickListener { openList(searchText()) }
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                openList(searchText())
                true
            } else false
        }
        binding.btnBrowse.setOnClickListener { openList("") }
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddEditRestaurantActivity::class.java))
        }
    }

    // Refresh whenever we come back to the Home screen
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val db = AppDatabase.get(this@MainActivity)
            topAdapter.submitList(
                db.restaurantDao().topRated(3).filter { it.reviewCount > 0 }
            )
            recentAdapter.submitList(db.reviewDao().recent(5).map { it.toRow() })
        }
    }

    private fun searchText() = binding.etSearch.text?.toString()?.trim().orEmpty()

    private fun openList(query: String) {
        startActivity(
            Intent(this, RestaurantListActivity::class.java).putExtra(EXTRA_QUERY, query)
        )
    }
}