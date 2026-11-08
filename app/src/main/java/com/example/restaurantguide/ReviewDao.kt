package com.example.restaurantguide

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReviewDao {

    @Insert
    suspend fun insert(review: Review)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId ORDER BY visitDate DESC")
    suspend fun forRestaurant(restaurantId: Long): List<Review>

    @Query(
        """
        SELECT v.id AS id, v.rating AS rating, v.comment AS comment,
               v.visitDate AS visitDate, r.name AS restaurantName
        FROM reviews v JOIN restaurants r ON r.id = v.restaurantId
        ORDER BY v.visitDate DESC
        LIMIT :limit
        """
    )
    suspend fun recent(limit: Int): List<ReviewWithRestaurant>
}