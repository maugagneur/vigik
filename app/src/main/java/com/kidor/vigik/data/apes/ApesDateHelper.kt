package com.kidor.vigik.data.apes

import timber.log.Timber
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Helper for dates and time logic related to APES API.
 */
object ApesDateHelper {

    /**
     * Paris time zone reference.
     */
    private val PARIS_ZONE: ZoneId = ZoneId.of("Europe/Paris")
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private const val NIGHT_CUTOFF_HOUR = 3
    private const val EVENING_START_HOUR = 20
    private const val MINUTES_PER_HOUR = 60
    private const val PAD_TWO_DIGITS = 2

    /**
     * Returns the date of the current evening in Paris timezone.
     * Before 03:00 AM, the current evening belongs to yesterday.
     */
    fun getCurrentEveningDate(now: ZonedDateTime = ZonedDateTime.now(PARIS_ZONE)): LocalDate {
        val parisTime = now.withZoneSameInstant(PARIS_ZONE)
        return if (parisTime.hour < NIGHT_CUTOFF_HOUR) {
            parisTime.toLocalDate().minusDays(1)
        } else {
            parisTime.toLocalDate()
        }
    }

    /**
     * Formats date to YYYY-MM-DD.
     */
    fun formatDate(date: LocalDate): String {
        return date.format(DATE_FORMATTER)
    }

    /**
     * Parses an ISO UTC string (e.g. 2026-10-13T22:13:30.460056Z) into an [Instant], or returns null if invalid.
     */
    fun parseInstant(isoUtc: String?): Instant? {
        if (isoUtc.isNullOrBlank()) return null
        return try {
            Instant.parse(isoUtc)
        } catch (exception: DateTimeParseException) {
            Timber.d(exception, "Error parsing instant: $isoUtc")
            null
        }
    }

    /**
     * Formats elapsed duration since an ISO UTC timestamp to a human readable string (e.g. "45 min" or "1 h 15 min").
     */
    fun formatDurationSince(sinceIsoUtc: String?, nowInstant: Instant = Instant.now()): String {
        val start = parseInstant(sinceIsoUtc) ?: return ""
        val duration = Duration.between(start, nowInstant)
        val minutes = duration.toMinutes().coerceAtLeast(0)
        val hours = minutes / MINUTES_PER_HOUR
        val remMinutes = minutes % MINUTES_PER_HOUR
        return if (hours > 0) {
            "${hours}h ${remMinutes.toString().padStart(PAD_TWO_DIGITS, '0')}"
        } else {
            "$minutes min"
        }
    }

    /**
     * Checks if a local slot ISO string (e.g. 2026-10-13T21:30) belongs to the evening of [eveningDate]
     * (20h00 to 03h00 next day).
     */
    fun isSlotInEvening(slotLocal: String?, eveningDate: LocalDate): Boolean {
        if (slotLocal.isNullOrBlank()) return false
        return try {
            val dateTime = LocalDateTime.parse(slotLocal)
            (dateTime.toLocalDate() == eveningDate && dateTime.hour >= EVENING_START_HOUR) ||
                (dateTime.toLocalDate() == eveningDate.plusDays(1) && dateTime.hour < NIGHT_CUTOFF_HOUR)
        } catch (exception: DateTimeParseException) {
            Timber.d(exception, "Error parsing slot local date")
            false
        }
    }

    /**
     * Formats slot local string (e.g. "2026-10-13T21:30") to a short hour display "21h30".
     */
    fun formatSlotHour(slotLocal: String?): String {
        if (slotLocal.isNullOrBlank()) return ""
        return try {
            val dateTime = LocalDateTime.parse(slotLocal)
            "${dateTime.hour}h${dateTime.minute.toString().padStart(PAD_TWO_DIGITS, '0')}"
        } catch (exception: DateTimeParseException) {
            Timber.d(exception, "Error formatting slot hour")
            slotLocal
        }
    }
}
