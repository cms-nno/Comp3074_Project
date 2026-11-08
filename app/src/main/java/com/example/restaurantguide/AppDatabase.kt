package com.example.restaurantguide

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Restaurant::class, Review::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "restaurant_guide.db"
                ).addCallback(seedCallback).build().also { INSTANCE = it }
            }

        // Runs once, when the database file is first created
        private val seedCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL("INSERT INTO restaurants (name, address, description, tags, latitude, longitude) VALUES ('Sample Sushi Bar', '123 Queen St W, Toronto, ON', 'Casual sushi spot with great lunch specials', 'sushi,japanese,lunch', 43.6503, -79.3871)")
                db.execSQL("INSERT INTO restaurants (name, address, description, tags, latitude, longitude) VALUES ('Sample Pizza Place', '456 King St W, Toronto, ON', 'Wood-fired pizza and fresh pasta', 'pizza,italian,dinner', 43.6446, -79.3955)")
                db.execSQL("INSERT INTO restaurants (name, address, description, tags, latitude, longitude) VALUES ('Sample Brunch Cafe', '789 College St, Toronto, ON', 'Popular weekend brunch and good coffee', 'brunch,cafe,coffee', 43.6548, -79.4136)")

                db.execSQL("INSERT INTO reviews (restaurantId, rating, comment, visitDate) VALUES (1, 5, 'Fresh fish and friendly staff.', 1730000000000)")
                db.execSQL("INSERT INTO reviews (restaurantId, rating, comment, visitDate) VALUES (1, 4, 'Great lunch deal, a bit busy.', 1730500000000)")
                db.execSQL("INSERT INTO reviews (restaurantId, rating, comment, visitDate) VALUES (2, 4, 'Crispy crust, would come back.', 1730200000000)")
                db.execSQL("INSERT INTO reviews (restaurantId, rating, comment, visitDate) VALUES (3, 3, 'Good coffee but long wait for a table.', 1730400000000)")
            }
        }
    }
}