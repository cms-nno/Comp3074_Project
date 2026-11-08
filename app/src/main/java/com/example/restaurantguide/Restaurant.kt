package com.example.restaurantguide

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String,
    val description: String,
    val tags: String,              // comma-separated, e.g. "sushi,lunch"
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUri: String? = null   // optional, not used yet
)