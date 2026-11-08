package com.example.restaurantguide

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RestaurantDao {

    @Insert
    suspend fun insert(restaurant: Restaurant): Long

    @Update
    suspend fun update(restaurant: Restaurant)

    @Delete
    suspend fun delete(restaurant: Restaurant)

    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun getById(id: Long): Restaurant?

    // Search by name or tag. Pass "" to get every restaurant.
    @Query(
        """
        SELECT r.*, COALESCE(AVG(v.rating), 0.0) AS avgRating, COUNT(v.id) AS reviewCount
        FROM restaurants r LEFT JOIN reviews v ON v.restaurantId = r.id
        WHERE r.name LIKE '%' || :query || '%' OR r.tags LIKE '%' || :query || '%'
        GROUP BY r.id
        ORDER BY r.name COLLATE NOCASE
        """
    )
    suspend fun search(query: String): List<RestaurantWithRating>

    @Query(
        """
        SELECT r.*, COALESCE(AVG(v.rating), 0.0) AS avgRating, COUNT(v.id) AS reviewCount
        FROM restaurants r LEFT JOIN reviews v ON v.restaurantId = r.id
        GROUP BY r.id
        ORDER BY avgRating DESC, reviewCount DESC
        LIMIT :limit
        """
    )
    suspend fun topRated(limit: Int): List<RestaurantWithRating>

    @Query(
        """
        SELECT r.*, COALESCE(AVG(v.rating), 0.0) AS avgRating, COUNT(v.id) AS reviewCount
        FROM restaurants r LEFT JOIN reviews v ON v.restaurantId = r.id
        WHERE r.id = :id
        GROUP BY r.id
        """
    )
    suspend fun getWithRating(id: Long): RestaurantWithRating?
}