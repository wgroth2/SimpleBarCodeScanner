/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository module for handling data operations on the scan history.
 *
 * This class abstracts the data source (the [ScanHistoryDao]) from the rest of the application,
 * providing a clean API for data access.
 *
 * @param scanHistoryDao The Data Access Object for scan history.
 */
class ScanHistoryRepository(private val scanHistoryDao: ScanHistoryDao) {

    /**
     * A [Flow] that provides a real-time stream of all scan history records,
     * ordered from newest to oldest.
     */
    val allScans: Flow<List<Scan>> = scanHistoryDao.getAllScans()

    /**
     * Suspended function to insert a new scan record into the database.
     *
     * @param scan The [Scan] object to be inserted.
     */
    suspend fun insert(scan: Scan) {
        scanHistoryDao.insertScan(scan)
    }

    /**
     * Suspended function to delete all scan records from the database.
     */
    suspend fun clearAll() {
        scanHistoryDao.clearAll()
    }
}
