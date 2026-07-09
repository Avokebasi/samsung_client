package com.cattery.presentation.util

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

object AgeFormatter {
    fun format(birthDate: String): String = runCatching {
        val iso = DateFormatter.toIso(birthDate.trim()) ?: birthDate.trim()
        val birth = LocalDate.parse(iso)
        val today = LocalDate.now()
        if (birth.isAfter(today)) return@runCatching ""
        val period = Period.between(birth, today)
        buildString {
            when {
                period.years > 0 -> {
                    append("${period.years} г.")
                    if (period.months > 0) append(" ${period.months} мес.")
                }
                period.months > 0 -> {
                    append("${period.months} мес.")
                    if (period.days > 0) append(" ${period.days} дн.")
                }
                else -> {
                    val days = ChronoUnit.DAYS.between(birth, today).coerceAtLeast(0)
                    append("$days дн.")
                }
            }
        }
    }.getOrDefault("")
}
