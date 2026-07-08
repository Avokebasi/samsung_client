package com.cattery.presentation.util

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

object AgeFormatter {
    fun format(birthDate: String): String = runCatching {
        val birth = LocalDate.parse(birthDate)
        val today = LocalDate.now()
        val period = Period.between(birth, today)
        when {
            period.years > 0 -> "${period.years} г."
            period.months > 0 -> "${period.months} мес."
            else -> "${ChronoUnit.DAYS.between(birth, today)} дн."
        }
    }.getOrDefault("")
}
