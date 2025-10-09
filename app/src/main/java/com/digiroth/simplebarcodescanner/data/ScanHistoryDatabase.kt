/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for the application, which contains the "scan_history" table.
 *
 * This class is abstract and is implemented by Room at compile time.
 */
@Database(entities = [Scan::class], version = 1, exportSchema = false)
abstract class ScanHistoryDatabase : RoomDatabase() {

    /**
     * Provides an instance of the [ScanHistoryDao].
     *
     * @return The Data Access Object for the scan history table.
     */
    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: ScanHistoryDatabase? = null

        /**
         * Returns a singleton instance of the [ScanHistoryDatabase].
         *
         * This method handles the creation of the database if it does not already exist,
         * ensuring that only one instance of the database is ever created.
         *
         * @param context The application context.
         * @return The singleton [ScanHistoryDatabase] instance.
         */
        fun getDatabase(context: Context): ScanHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScanHistoryDatabase::class.java,
                    "scan_history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
