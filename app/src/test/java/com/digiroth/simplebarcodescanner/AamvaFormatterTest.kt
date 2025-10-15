/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner

import com.digiroth.simplebarcodescanner.utils.AamvaFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class AamvaFormatterTest {

    @Test
    fun `formatAamvaData correctly parses and formats valid data`() {
        // Arrange: A sample AAMVA data string.
        val rawData = """
            @
            ANSI 636026080102DL00410288ZA03290015DLDCADACSMITH,JOHN,MIDDLE
            DCBDNONE
            DCDNONE
            DBA20251015
            DCSSMITH
            DACJOHN
            DADMIDDLE
            DBD20171015
            DBB19900520
            DBC1
            DAYBRO
            DAU070 IN
            DAG123 MAIN ST
            DAIANYTOWN
            DAJCA
            DAK90210
            DAQ12345678
            DCF12345678901234567890
            DCGUSA
            DAZBRO
        """.trimIndent()

        // Act: Call the function under test.
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)

        // Assert: Verify the output is as expected.
        val expectedOutput = """
            First Name: JOHN
            Last Name: SMITH
            Date of Birth: 05/20/1990
            Sex: Male
            License Number: 12345678
            Expiration Date: 10/15/2025
            Height: 5' 10"
            Eye Color: BRO
            Address: 123 MAIN ST, ANYTOWN, CA 90210
        """.trimIndent()

        assertEquals(expectedOutput, formattedResult)
    }

    @Test
    fun `formatAamvaData handles null input gracefully`() {
        // Arrange
        val rawData: String? = null

        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)

        // Assert
        assertEquals("No data provided.", formattedResult)
    }

    @Test
    fun `formatAamvaData handles non-AAMVA data`() {
        // Arrange
        val rawData = "This is just a regular barcode."

        // Act
        val formattedResult = AamvaFormatter.formatAamvaData(rawData)

        // Assert
        assertEquals("This data does not appear to be in a recognized AAMVA format.", formattedResult)
    }
}
