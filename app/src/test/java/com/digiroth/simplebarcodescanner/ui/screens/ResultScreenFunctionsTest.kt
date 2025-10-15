/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.ui.screens

import android.content.Context
import com.digiroth.simplebarcodescanner.R
import com.digiroth.simplebarcodescanner.utils.AamvaFormatter
import com.google.mlkit.vision.barcode.common.Barcode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ResultScreenFunctionsTest {

    @Mock
    private lateinit var mockContext: Context

    // --- Tests for getBarcodeFormatName ---

    @Test
    fun `getBarcodeFormatName returns correct string for QR Code`() {
        assertEquals("QR Code", getBarcodeFormatName(Barcode.FORMAT_QR_CODE))
    }

    @Test
    fun `getBarcodeFormatName returns correct string for Code 128`() {
        assertEquals("Code 128", getBarcodeFormatName(Barcode.FORMAT_CODE_128))
    }

    @Test
    fun `getBarcodeFormatName returns 'Unknown Format' for an unsupported format`() {
        assertEquals("Unknown Format", getBarcodeFormatName(-1))
    }
}
