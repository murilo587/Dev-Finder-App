package com.example.devfinder.feature.publicrepos

import java.time.ZonedDateTime

fun formatDate(date: String): String {
    val parsed = ZonedDateTime.parse(date)

    val months = mapOf(
        1 to "Jan",
        2 to "Fev",
        3 to "Mar",
        4 to "Abr",
        5 to "Mai",
        6 to "Jun",
        7 to "Jul",
        8 to "Ago",
        9 to "Set",
        10 to "Out",
        11 to "Nov",
        12 to "Dez"
    )

    val day = parsed.dayOfMonth
    val month = parsed.monthValue
    val year = parsed.year

    val monthName = months[month] ?: ""

    return "$day $monthName, $year"
}