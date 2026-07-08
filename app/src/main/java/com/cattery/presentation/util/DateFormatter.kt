package com.cattery.presentation.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateFormatter {
    private val displayFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun formatDisplay(isoDate: String): String = runCatching {
        LocalDate.parse(isoDate).format(displayFormat)
    }.getOrDefault(isoDate)
}
