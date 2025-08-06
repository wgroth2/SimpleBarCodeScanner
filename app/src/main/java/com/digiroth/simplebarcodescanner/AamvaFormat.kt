package com.digiroth.simplebarcodescanner

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Singleton object responsible for parsing and pretty-printing
 * raw AAMVA (American Association of Motor Vehicle Administrators)
 * DL/ID card data strings.
 */
object AamvaFormatter {
    // A map of common AAMVA Data Element Identifiers (DEIs) to their friendly names.
    // This is not exhaustive and can be expanded based on the AAMVA standard version you target.
    val AAMVA_FIELD_MAP = mapOf(
        // Common Demographics
        "DCA" to "Jurisdiction Code", // Vehicle/ID jurisdiction
        "DCB" to "Jurisdiction-specific classification codes",
        "DCD" to "Jurisdiction-specific endorsement codes",
        "DBA" to "Date of Expiration", // YYYYMMDD or MMDDYYYY depending on version/jurisdiction
        "DCS" to "Last Name", // Or Family Name
        "DAC" to "First Name", // Or Given Name
        "DAD" to "Middle Name(s)",
        "DBD" to "Date of Issue", // YYYYMMDD or MMDDYYYY
        "DBB" to "Date of Birth", // YYYYMMDD or MMDDYYYY
        "DBC" to "Sex", // 1 = Male, 2 = Female, 9 = Not Specified
        "DAY" to "Eye Color",
        "DAU" to "Height", // e.g., 0600 IN (6 feet 0 inches) or CM
        "DAG" to "Address Street 1",
        "DAH" to "Address Street 2",
        "DAI" to "Address City",
        "DAJ" to "Address State", // Or Jurisdiction
        "DAK" to "Address Postal Code", // ZIP code
        "DAQ" to "Customer ID Number", // aka License Number
        "DCF" to "Document Discriminator", // Unique identifier for the document
        "DCG" to "Country Identification", // USA or CAN
        "DDE" to "Last Name Truncation",
        "DDF" to "First Name Truncation",
        "DDG" to "Middle Name Truncation",

        // Driver Specific
        "DAZ" to "Hair Color",
        "DCI" to "Place of Birth",
        "DCJ" to "Audit Information",
        "DCK" to "Inventory Control Number",
        "DBN" to "Full Name", // May not always be present
        "DBG" to "Full Address", // May not always be present
        "DDA" to "Compliance Type",
        "DDB" to "Card Revision Date",
        "DDC" to "HazMat Endorsement Expiration Date",
        "DDD" to "Limited Duration Document Indicator",
        "DAW" to "Weight (pounds)",
        "DAX" to "Weight (kilograms)",
        "DDH" to "Under 18 Until Date",
        "DDI" to "Under 19 Until Date",
        "DDJ" to "Under 21 Until Date",
        "DDK" to "Organ Donor Indicator",
        "DDL" to "Veteran Indicator",

        // Other commonly found
        "DAA" to "Customer Full Name" // Often an aggregation
        // Add more fields as needed from AAMVA specs
    )

    // Date formats that might be encountered. AAMVA spec often uses CCYYMMDD or MMDDCCYY.
// The spec is sometimes inconsistent across versions for date order.
// This example prioritizes YYYYMMDD then MMDDYYYY.
    private val INPUT_DATE_FORMATS = listOf(
        SimpleDateFormat("yyyyMMdd", Locale.US),
        SimpleDateFormat("MMddyyyy", Locale.US)
    )
    private val OUTPUT_DATE_FORMAT = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    fun formatAamvaData(rawData: String): String {
        if (rawData.isBlank()) {
            return "No data provided."
        }

        val prettyPrinted = StringBuilder()
        // AAMVA data usually starts with "@\n\u001e\r" (ANSI header) then "AAMVA..."
        // We'll split by newline, common in scanned data after the header.
        val lines = rawData.split('\n', '\r').map { it.trim() }.filter { it.isNotEmpty() }

        var isAamvaFormat = false
        if (lines.any { it.startsWith("ANSI ") }) { // Check for ANSI header line
            isAamvaFormat = true
        } else if (lines.any { it.startsWith("AAMVA") }) { // Some scanners might provide this as first data line
            isAamvaFormat = true
        } else if (lines.firstOrNull()
                ?.let { it.length > 3 && AAMVA_FIELD_MAP.containsKey(it.substring(0, 3)) } == true
        ) {
            // Fallback: If the first line looks like a field ID, assume it's AAMVA data.
            // This is less reliable.
            isAamvaFormat = true
        }

        if (!isAamvaFormat && lines.firstOrNull()?.startsWith("@") != true) {
            // A simple heuristic: if it doesn't look like AAMVA format and doesn't start with @
            // return raw data or a message. You might want to be more lenient.
            return "Data does not appear to be in a recognized AAMVA format.\nRaw Data:\n$rawData"
            // For this example, we'll try to parse anyway if it doesn't explicitly fail above checks.
        }


        for (line in lines) {
            if (line.length < 3) continue // Not long enough for an ID

            val fieldId = line.substring(0, 3)
            val value = if (line.length > 3) line.substring(3).trim() else ""

            AAMVA_FIELD_MAP[fieldId]?.let { fieldName ->
                val formattedValue = when (fieldId) {
                    "DBA", "DBD", "DBB", "DDB", "DDC", "DDH", "DDI", "DDJ" -> formatDate(value)
                    "DBC" -> formatSex(value)
                    "DAU" -> formatHeight(value)
                    // Add other specific formatters if needed
                    else -> value
                }
                if (formattedValue.isNotEmpty()) {
                    prettyPrinted.append("$fieldName: $formattedValue\n")
                }
            }
        }

        return if (prettyPrinted.isNotEmpty()) {
            prettyPrinted.toString().trim()
        } else {
            "Could not parse AAMVA data. Raw data might be malformed or not AAMVA compliant.\nInput:\n$rawData"
        }
    }

    private fun formatDate(dateStr: String): String {
        for (format in INPUT_DATE_FORMATS) {
            try {
                val date = format.parse(dateStr)
                if (date != null) {
                    return OUTPUT_DATE_FORMAT.format(date)
                }
            } catch (e: Exception) {
                // Ignore and try next format
            }
        }
        return dateStr // Return original if parsing fails
    }

    private fun formatSex(sexCode: String): String {
        return when (sexCode) {
            "1" -> "Male"
            "2" -> "Female"
            "9" -> "Not Specified"
            else -> sexCode // Return original if unknown
        }
    }

    private fun formatHeight(heightStr: String): String {
        // Example: "0600 IN" for 6ft 0in, or "180 CM"
        // This is a simplified formatter. AAMVA spec can be more complex.
        if (heightStr.endsWith(" IN")) {
            try {
                val inchesTotal = heightStr.substring(0, heightStr.length - 3).toInt()
                val feet =
                    inchesTotal / 100 // Older specs might use 3 digits, first for feet. Newer 4 digits CCYY
                val inches = inchesTotal % 100
                if (feet > 0) return "$feet ft $inches in"
                return "$inches in"
            } catch (e: NumberFormatException) { /* ignore */
            }
        } else if (heightStr.endsWith(" CM")) {
            try {
                val cm = heightStr.substring(0, heightStr.length - 3).toInt()
                return "$cm cm"
            } catch (e: NumberFormatException) { /* ignore */
            }
        }
        return heightStr // Return original if not in expected format
    }
}
// --- How to use it in your ResultsActivity ---
// In your ResultsActivity's onCreate or where you display the result:
//
// val scannedData = intent.getStringExtra(EXTRA_SCAN_RESULT) ?: "No data received"
// val prettyPrintedData = if (scannedData.startsWith("@") || scannedData.contains("AAMVA")) {
//     formatAamvaData(scannedData)
// } else {
//     scannedData // Or some other handling for non-AAMVA data
// }
//
// // Then use prettyPrintedData in your Text composable
// ResultScreen(scannedData = prettyPrintedData, ...)

