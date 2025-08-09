package com.digiroth.simplebarcodescanner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AamvaFormatterTest {

    @Test
    fun `formatAamvaData with valid full data string formats correctly`() {
        // Arrange
        val rawData = """
            @
            ANSI 636026080102DL00410288ZV03210217DLDCADCSDOE
            DACJOHN
            DCSDOE
            DBA20291231
            DBB19900115
            DBC1
            DAU069IN
            DAYBRO
            DAG123 MAIN ST
            DAIANYTOWN
            DAJCA
            DAK902100000
            DAQ123456789
            """.trimIndent()

        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)
        val resultLines = formattedResult.lines().associate {
            val parts = it.split(": ", limit = 2)
            parts[0] to parts[1]
        }

        // Assert: Granular check on each line to isolate the failure.
        assertEquals("JOHN", resultLines["First Name"])
        assertEquals("DOE", resultLines["Last Name"])
        assertEquals("01/15/1990", resultLines["Date of Birth"])
        assertEquals("Male", resultLines["Sex"])
        assertEquals("123456789", resultLines["License Number"])
        assertEquals("12/31/2029", resultLines["Expiration Date"])
        assertEquals("5' 9\"", resultLines["Height"])
        assertEquals("BRO", resultLines["Eye Color"])
        assertEquals("123 MAIN ST, ANYTOWN, CA 902100000", resultLines["Address"])
    }

    @Test
    fun `formatAamvaData with nonAamva data returns unrecognized format message`() {
        // Arrange
        val rawData = "This is just a regular barcode"
        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)
        // Assert
        assertEquals("This data does not appear to be in a recognized AAMVA format.", formattedResult)
    }

    @Test
    fun `formatAamvaData with blank input returns no data message`() {
        // Arrange
        val rawData = ""
        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)
        // Assert
        assertEquals("No data provided.", formattedResult)
    }

    @Test
    fun `formatAamvaData with null input returns no data message`() {
        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(null)
        // Assert
        assertEquals("No data provided.", formattedResult)
    }

    @Test
    fun `formatAamvaData formats female sex correctly`() {
        // Arrange
        val rawData = "DBC2"
        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)
        // Assert
        assertTrue(formattedResult.contains("Sex: Female"))
    }

    @Test
    fun `formatAamvaData formats height correctly in feet and inches`() {
        // Arrange
        val rawData = "DAU070IN" // 70 inches = 5' 10"
        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)
        // Assert
        assertTrue(formattedResult.contains("Height: 5' 10\""))
    }
}
