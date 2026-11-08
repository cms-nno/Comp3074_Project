package com.example.restaurantguide

import java.util.Locale
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Keys used to pass data between screens
const val EXTRA_RESTAURANT_ID = "restaurant_id"
const val EXTRA_QUERY = "query"

// "★ 4.5  (2 reviews)" or "No ratings yet"
fun ratingLabel(avg: Double, count: Int): String =
    if (count == 0) "No ratings yet"
    else String.format(Locale.getDefault(), "★ %.1f  (%d %s)", avg, count, if (count == 1) "review" else "reviews")

// "sushi, lunch" -> "#sushi  #lunch"
fun tagsLabel(tags: String): String =
    tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.joinToString("  ") { "#$it" }
// Keeps screen content below the status bar + title bar, and above the keyboard / gesture bar
fun AppCompatActivity.applySystemBarPadding(root: View) {
    val tv = TypedValue()
    theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)
    val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)

    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        v.setPadding(bars.left, bars.top + actionBarHeight, bars.right, maxOf(bars.bottom, ime.bottom))
        insets
    }
}