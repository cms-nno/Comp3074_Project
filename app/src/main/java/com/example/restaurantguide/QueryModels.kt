package com.example.restaurantguide

import androidx.room.Embedded

// A restaurant plus its average rating (calculated, not stored)
data class RestaurantWithRating(
    @Embedded val restaurant: Restaurant,
    val avgRating: Double,
    val reviewCount: Int
)

// A review plus the name of the restaurant it belongs to (for the Home screen)
data class ReviewWithRestaurant(
    val id: Long,
    val rating: Int,
    val comment: String,
    val visitDate: Long,
    val restaurantName: String
)