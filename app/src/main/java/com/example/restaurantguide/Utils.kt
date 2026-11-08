package com.example.restaurantguide

import java.util.Locale

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