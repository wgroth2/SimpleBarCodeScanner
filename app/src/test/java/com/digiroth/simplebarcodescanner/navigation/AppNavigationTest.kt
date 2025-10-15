/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.navigation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URLEncoder

class AppNavigationTest {

    @Test
    fun `createRoute correctly formats the navigation string`() {
        // Arrange
        val scanResult = "12345"
        val valueType = 7 // Corresponds to Barcode.TYPE_URL
        val format = 8    // Corresponds to Barcode.FORMAT_QR_CODE

        // Act
        val route = Screen.Results.createRoute(scanResult, valueType, format)

        // Assert
        assertEquals("results_screen/12345/7/8", route)
    }

    @Test
    fun `createRoute correctly encodes special characters in scanResult`() {
        // Arrange
        val scanResultWithSpecialChars = "https://example.com?id=1&name=test"
        val valueType = 7
        val format = 8

        // The expected result after standard URL encoding
        val encodedResult = URLEncoder.encode(scanResultWithSpecialChars, "UTF-8")

        // Act
        val route = Screen.Results.createRoute(scanResultWithSpecialChars, valueType, format)

        // Assert
        assertEquals("results_screen/$encodedResult/$valueType/$format", route)
    }

    @Test
    fun `createRoute handles empty scanResult`() {
        // Arrange
        val emptyResult = ""
        val valueType = 10 // Corresponds to Barcode.TYPE_TEXT
        val format = 1

        // Act
        val route = Screen.Results.createRoute(emptyResult, valueType, format)

        // Assert
        assertEquals("results_screen//10/1", route)
    }
}
