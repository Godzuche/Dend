package com.godzuche.dend.core.presentation.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

fun Instant.toFriendlyStringTimeOnly(): String {
    val now = Clock.System.now()
    val duration = now - this

    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Rule 1: Less than a minute ago
    if (duration.inWholeSeconds < 60) {
        return "Just now"
    }

    // Rule 2: Within the last hour
    if (duration.inWholeMinutes < 60) {
        return "${duration.inWholeMinutes} minutes ago"
    }

    return localDateTime.toAmPmTime()
}

/**
 * A sophisticated extension function that formats an Instant into a user-friendly,
 * relative, and context-aware string.
 */
fun Instant.toFriendlyString(): String {
    val now = Clock.System.now()
    val duration = now - this

    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Rule 1: Less than a minute ago
    if (duration.inWholeSeconds < 60) {
        return "Just now"
    }

    // Rule 2: Within the last hour
    if (duration.inWholeMinutes < 60) {
        return "${duration.inWholeMinutes} minutes ago"
    }

    // Rule 3: Happened today
    if (localDateTime.date == today) {
        return "Today at ${localDateTime.toAmPmTime()}"
    }

    // Rule 4: Happened yesterday
    val yesterday = today.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
    if (localDateTime.date == yesterday) {
        return "Yesterday at ${localDateTime.toAmPmTime()}"
    }

    // Rule 5: Within the last week
    if (duration < 7.days) {
        // Returns "Sunday", "Monday", etc.
        val dayOfWeek = localDateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() }
        return "$dayOfWeek at ${localDateTime.toAmPmTime()}"
    }

    // Rule 6: Older than a week
    val month = localDateTime.month.name.lowercase().replaceFirstChar { it.titlecase() }.take(3)
    val day = localDateTime.day

    // Optional: Include year if it's not the current year
    return if (localDateTime.year != today.year) {
        "$month $day, ${localDateTime.year}"
    } else {
        "$month $day"
    }
}

/**
 * Helper function to format the time part of a LocalDateTime into a 12-hour AM/PM format.
 * Example: 14:30 -> "2:30 PM"
 */
private fun LocalDateTime.toAmPmTime(): String {
    val hour12 = if (this.hour == 0 || this.hour == 12) 12 else this.hour % 12
    val minutePadded = this.minute.toString().padStart(2, '0')
    val amPm = if (this.hour < 12) "AM" else "PM"
    return "$hour12:$minutePadded $amPm"
}

/**
 * A helper function to determine the header string ("Today", "Yesterday", etc.)
 * for a given Instant.
 */
fun Instant.toFriendlyDateHeader(): String {
    val now = Clock.System.now()
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val yesterday = today.minus(1, DateTimeUnit.DAY)

    return when (localDateTime.date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> {
            // e.g., "Tuesday, September 17"
            val dayOfWeek =
                localDateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() }
            val month =
                localDateTime.month.name.lowercase().replaceFirstChar { it.titlecase() }/*.take(3)*/
            val day = localDateTime.day
            "$dayOfWeek, $month $day"
        }
    }
}