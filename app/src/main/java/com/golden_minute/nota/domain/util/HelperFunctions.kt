package com.golden_minute.nota.domain.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun compareDateTimes(date1: String, date2: String, format: String): Int {
    val formatter = DateTimeFormatter.ofPattern(format)
    val dateTime1 = LocalDateTime.parse(date1, formatter)
    val dateTime2 = LocalDateTime.parse(date2, formatter)

    return when {
        dateTime1 == null || dateTime2 == null -> 0
        dateTime1.isBefore(dateTime2) -> -1
        dateTime1.isAfter(dateTime2) -> 1
        else -> 0
    }
}

fun leadingZero(date: String): String {
    return if (date.toInt() < 10) "0$date" else date
}

fun customInterval(hours: Int): Long  =  hours * 60 * 60 * 1000L
