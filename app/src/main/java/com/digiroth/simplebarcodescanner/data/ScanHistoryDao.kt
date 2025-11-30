/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the scan history database.
 * Provides methods for interacting with the "scan_history" table.
 */
@Dao
interface ScanHistoryDao {
    /**
     * Retrieves all scans from the database, ordered by timestamp in descending order (newest first).
     *
     * @return A [Flow] that emits a list of all [Scan] entities whenever the data changes.
     */
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<Scan>>

    /**
     * Inserts a new scan into the database. If a scan with the same primary key already exists,
     * it will be replaced.
     *
     * @param scan The [Scan] entity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: Scan)

    /**
     * Deletes all entries from the "scan_history" table.
     */
    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}
