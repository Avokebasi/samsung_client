package com.cattery.presentation.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    private val displayFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    fun formatDisplay(isoDate: String): String = runCatching {
        LocalDate.parse(isoDate).format(displayFormat)
    }.getOrDefault(isoDate)

    fun formatDateTime(isoDateTime: String): String = runCatching {
        Instant.parse(isoDateTime)
            .atZone(ZoneId.systemDefault())
            .format(dateTimeFormat)
    }.getOrDefault(isoDateTime)
}
