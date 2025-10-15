/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.ui.screens

import android.content.Context
import com.digiroth.simplebarcodescanner.R
import com.google.mlkit.vision.barcode.common.Barcode
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ResultScreenTest {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        // Define the behavior of our mock context
        whenever(mockContext.getString(R.string.barcode_type_url)).thenReturn("URL")
        whenever(mockContext.getString(R.string.barcode_type_text)).thenReturn("Text")
        whenever(mockContext.getString(R.string.barcode_type_drivers_license)).thenReturn("Driver's License")
        whenever(mockContext.getString(R.string.barcode_type_unknown)).thenReturn("Unknown")
    }

    @Test
    fun `getBarcodeTypeName returns correct string for URL`() {
        // Act
        val result = getBarcodeTypeName(mockContext, Barcode.TYPE_URL)
        // Assert
        assertEquals("URL", result)
    }

    @Test
    fun `getBarcodeTypeName returns correct string for Text`() {
        // Act
        val result = getBarcodeTypeName(mockContext, Barcode.TYPE_TEXT)
        // Assert
        assertEquals("Text", result)
    }

    @Test
    fun `getBarcodeTypeName returns correct string for Driver's License`() {
        // Act
        val result = getBarcodeTypeName(mockContext, Barcode.TYPE_DRIVER_LICENSE)
        // Assert
        assertEquals("Driver's License", result)
    }

    @Test
    fun `getBarcodeTypeName returns 'Unknown' for an unsupported type`() {
        // Arrange: An arbitrary integer that doesn't map to a known type
        val unknownType = 999

        // Act
        val result = getBarcodeTypeName(mockContext, unknownType)

        // Assert
        assertEquals("Unknown", result)
    }
}
