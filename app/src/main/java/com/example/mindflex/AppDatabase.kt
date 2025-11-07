package com.example.mindflex

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        // Volatile ensures that the instance is always up-to-date and
        // the same to all execution threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if one exists
            // If not, create the database in a synchronized block
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindflex_database"
                )
                    .fallbackToDestructiveMigration() // Handle migrations simply
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}