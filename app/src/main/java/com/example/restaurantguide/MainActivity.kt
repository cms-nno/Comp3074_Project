package com.example.restaurantguide

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Temporary database test (removed in stage 6)
        val tv = findViewById<TextView>(R.id.tvHello)
        lifecycleScope.launch {
            val list = AppDatabase.get(this@MainActivity).restaurantDao().search("")
            tv.text = list.joinToString("\n") {
                "${it.restaurant.name}  ★${"%.1f".format(it.avgRating)} (${it.reviewCount})"
            }
        }
    }
}