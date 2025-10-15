/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test for the [ScanHistoryDatabase] and [ScanHistoryDao].
 * This test uses an in-memory database to ensure tests are hermetic and fast.
 */
@RunWith(AndroidJUnit4::class)
class ScanHistoryDatabaseTest {

    private lateinit var scanHistoryDao: ScanHistoryDao
    private lateinit var db: ScanHistoryDatabase

    /**
     * Creates an in-memory database instance before each test.
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ScanHistoryDatabase::class.java
        )
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        scanHistoryDao = db.scanHistoryDao()
    }

    /**
     * Closes the database instance after each test.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * Tests the core functionality of writing a [Scan] to the database
     * and reading it back to verify correctness.
     */
    @Test
    @Throws(Exception::class)
    fun writeScanAndReadInList() = runTest {
        // Arrange: Create a sample scan object.
        val scan = Scan(
            timestamp = System.currentTimeMillis(),
            format = Barcode.FORMAT_QR_CODE,
            valueType = Barcode.TYPE_URL,
            rawValue = "https://example.com"
        )

        // Act: Insert the scan into the database.
        scanHistoryDao.insertScan(scan)

        // Assert: Retrieve the scans and verify the inserted data.
        val allScans = scanHistoryDao.getAllScans().first() // Get the first emitted list from the Flow
        assertEquals(1, allScans.size) // Check if there's only one item
        assertEquals(allScans[0].rawValue, scan.rawValue) // Check if the content matches
    }
}
