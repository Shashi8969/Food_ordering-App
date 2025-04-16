package com.example.foodordring

import java.util.Locale

object StringHandling {
    fun String?.getFirstName(): String? {
        return this?.split(" ")?.firstOrNull()
    }
    fun String?.getFirstNameWithoutPrefix(): String? {
        val prefixes = listOf("Mr", "Sri", "Ms", "Mrs", "Dr") // Add more prefixes as needed
        return this?.split("\\s+".toRegex())?.filter { it.isNotEmpty() }?.let { words -> // Split by one or more spaces and filter out empty strings
            if (words.isNotEmpty() && words[0].removeSuffix(".").capitalize() in prefixes && words.size > 1) {
                words.getOrNull(1)?.replaceFirstChar { it.uppercaseChar() } // Get the second word if a prefix is found and capitalize the first letter
            } else {
                words.firstOrNull()?.replaceFirstChar { it.uppercaseChar() } // Get the first word if no prefix is found and capitalize the first letter
            }
        }
    }
    fun String?.formatName(locale: Locale = Locale.getDefault()): String? {
        return this?.replace("\\s+".toRegex(), " ")?.trim()?.let { formatted ->
            formatted.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            }
        }
    }
    fun String?.formatAddressEnhanced(locale: Locale = Locale.getDefault()): String? {
        return this?.trim()?.let { address ->
            address.split(",").joinToString(", ") { part ->
                part.trim().split(" ").joinToString(" ") { word ->
                    val lowercased = word.lowercase(locale)
                    when {
                        lowercased in setOf("state", "street","ward","city", "rd", "blvd", "rd", "ln", "colony", "nagar", "vihar", "marg") -> word.replaceFirstChar { it.titlecase(locale) } // Added Indian abbreviations
                        else -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() } // Standard capitalization
                    }
                }
            }
        }
    }

}