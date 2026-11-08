package com.example.restaurantguide

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    foreignKeys = [ForeignKey(
        entity = Restaurant::class,
        parentColumns = ["id"],
        childColumns = ["restaurantId"],
        onDelete = ForeignKey.CASCADE   // deleting a restaurant deletes its reviews
    )],
    indices = [Index("restaurantId")]
)
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val restaurantId: Long,
    val rating: Int,        // 1 to 5
    val comment: String,
    val visitDate: Long     // milliseconds since 1970
)