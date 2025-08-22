// noinspection Typo
@file:Suppress("SpellCheckingInspection")

package com.digiroth.simplebarcodescanner

import java.text.SimpleDateFormat
import java.util.Locale

object AamvaFormatter {
    private val AAMVA_FIELD_MAP = mapOf(
        "DCA" to "Jurisdiction Code",
        "DCB" to "Jurisdiction-specific classification codes",
        "DCD" to "Jurisdiction-specific endorsement codes",
        "DBA" to "Expiration Date",
        "DCS" to "Last Name",
        "DAC" to "First Name",
        "DAD" to "Middle Name(s)",
        "DBD" to "Issue Date",
        "DBB" to "Date of Birth",
        "DBC" to "Sex",
        "DAY" to "Eye Color",
        "DAU" to "Height",
        "DAG" to "Address", // Simplified to a single 'Address' field for aggregation
        "DAH" to "Address Street 2",
        "DAI" to "City",
        "DAJ" to "State",
        "DAK" to "Postal Code",
        "DAQ" to "License Number",
        "DCF" to "Document Discriminator",
        "DCG" to "Country",
        "DDE" to "Last Name Truncation",
        "DDF" to "First Name Truncation",
        "DDG" to "Middle Name Truncation",
        "DAZ" to "Hair Color",
        "DCI" to "Place of Birth",
        "DDH" to "Under 18 Until",
        "DDI" to "Under 19 Until",
        "DDJ" to "Under 21 Until",
        "DDK" to "Organ Donor",
        "DDL" to "Veteran"
    )

    private val INPUT_DATE_FORMATS = listOf(
        SimpleDateFormat("yyyyMMdd", Locale.US).apply { isLenient = false },
        SimpleDateFormat("MMddyyyy", Locale.US).apply { isLenient = false }
    )
    private val OUTPUT_DATE_FORMAT = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    // CORRECTED: Function now accepts a nullable String?
    fun formatAamvaData(rawData: String?): String {
        // CORRECTED: Check for null or blank input at the beginning.
        if (rawData.isNullOrBlank()) {
            return "No data provided."
        }

        // Split data into lines and trim whitespace.
        val lines = rawData.lines().map { it.trim() }.filter { it.isNotEmpty() }

        // Heuristic to check if the data is likely in AAMVA format.
        val isLikelyAamva = lines.any {
            it.startsWith("@") || it.startsWith("ANSI ") || it.startsWith("AAMVA") ||
                    (it.length > 3 && AAMVA_FIELD_MAP.containsKey(it.substring(0, 3)))
        }

        if (!isLikelyAamva) {
            return "This data does not appear to be in a recognized AAMVA format."
        }

        val parsedData = mutableMapOf<String, String>()
        for (line in lines) {
            if (line.length < 3) continue
            val fieldId = line.substring(0, 3)
            val value = if (line.length > 3) line.substring(3).trim() else ""
            if (value.isNotEmpty()) {
                parsedData[fieldId] = value
            }
        }

        // Combine address parts into a single string.
        val address = listOfNotNull(
            parsedData["DAG"], // Street
            parsedData["DAI"], // City
            parsedData["DAJ"]  // State
        ).joinToString(", ") + parsedData["DAK"]?.let { " $it" }.orEmpty() // Postal Code

        val prettyPrinted = StringBuilder()
        // Define the order for a more readable output.
        val displayOrder = listOf("DAC", "DCS", "DBB", "DBC", "DAQ", "DBA", "DAU", "DAY", "DAG")

        for (fieldId in displayOrder) {
            val fieldName = AAMVA_FIELD_MAP[fieldId] ?: continue
            val value = parsedData[fieldId] ?: continue

            val formattedValue = when (fieldId) {
                "DBA", "DBB" -> formatDate(value)
                "DBC" -> formatSex(value)
                "DAU" -> formatHeight(value)
                "DAG" -> if (address.isNotBlank()) address else continue
                else -> value
            }
            if (formattedValue.isNotEmpty()) {
                prettyPrinted.append("$fieldName: $formattedValue\n")
            }
        }

        return if (prettyPrinted.isNotEmpty()) {
            prettyPrinted.toString().trim()
        } else {
            "Could not parse any recognized AAMVA fields."
        }
    }

    private fun formatDate(dateStr: String): String {
        for (format in INPUT_DATE_FORMATS) {
            try {
                format.parse(dateStr)?.let { return OUTPUT_DATE_FORMAT.format(it) }
            } catch (_: Exception) {
                // Ignore and try next format
            }
        }
        return dateStr
    }

    private fun formatSex(sexCode: String): String {
        return when (sexCode) {
            "1" -> "Male"
            "2" -> "Female"
            else -> "Not Specified"
        }
    }

    private fun formatHeight(heightStr: String): String {
        // Corrected and simplified height parsing.
        val heightVal = heightStr.takeWhile { it.isDigit() }
        if (heightVal.isEmpty()) return heightStr

        return try {
            if (heightStr.endsWith("IN")) { // Inches
                val inchesTotal = heightVal.toInt()
                val feet = inchesTotal / 12
                val inches = inchesTotal % 12
                "$feet' $inches\""
            } else if (heightStr.endsWith("CM")) { // Centimeters
                "$heightVal cm"
            } else {
                heightStr
            }
        } catch (_: NumberFormatException) {
            heightStr
        }
    }
}
