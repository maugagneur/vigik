package com.kidor.vigik.data.apes

import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Unit tests for [ApesDateHelper].
 */
class ApesDateHelperTest {

    private val parisZone = ZoneId.of("Europe/Paris")

    @Test
    fun `test getCurrentEveningDate before 03h00 returns previous day`() {
        logTestName()

        val timeAt2AM = ZonedDateTime.of(2026, 10, 14, 2, 30, 0, 0, parisZone)
        val eveningDate = ApesDateHelper.getCurrentEveningDate(timeAt2AM)

        assertEquals(LocalDate.of(2026, 10, 13), eveningDate, "Evening date before 3am")
    }

    @Test
    fun `test getCurrentEveningDate at or after 03h00 returns current day`() {
        logTestName()

        val timeAt21PM = ZonedDateTime.of(2026, 10, 14, 21, 30, 0, 0, parisZone)
        val eveningDate = ApesDateHelper.getCurrentEveningDate(timeAt21PM)

        assertEquals(LocalDate.of(2026, 10, 14), eveningDate, "Evening date at 21h30")
    }

    @Test
    fun `test formatDate`() {
        logTestName()

        val date = LocalDate.of(2026, 10, 13)
        assertEquals("2026-10-13", ApesDateHelper.formatDate(date), "Formatted date")
    }

    @Test
    fun `test formatDurationSince`() {
        logTestName()

        val now = Instant.parse("2026-10-13T21:45:00Z")
        val since45Min = "2026-10-13T21:00:00Z"
        val since1h30 = "2026-10-13T20:15:00Z"

        assertEquals("45 min", ApesDateHelper.formatDurationSince(since45Min, now), "Duration 45 min")
        assertEquals("1h 30", ApesDateHelper.formatDurationSince(since1h30, now), "Duration 1h 30")
    }

    @Test
    fun `test isSlotInEvening`() {
        logTestName()

        val eveningDate = LocalDate.of(2026, 10, 13)

        // Slots in evening
        assertTrue(ApesDateHelper.isSlotInEvening("2026-10-13T20:00", eveningDate), "20h00 is in evening")
        assertTrue(ApesDateHelper.isSlotInEvening("2026-10-13T22:30", eveningDate), "22h30 is in evening")
        assertTrue(ApesDateHelper.isSlotInEvening("2026-10-14T01:30", eveningDate), "01h30 next day is in evening")

        // Slots outside evening
        assertFalse(ApesDateHelper.isSlotInEvening("2026-10-13T18:00", eveningDate), "18h00 is before evening")
        assertFalse(ApesDateHelper.isSlotInEvening("2026-10-14T03:30", eveningDate), "03h30 next day is past evening")
        assertFalse(ApesDateHelper.isSlotInEvening("2026-10-15T21:00", eveningDate), "Two days later is not tonight")
    }

    @Test
    fun `test formatSlotHour`() {
        logTestName()

        assertEquals("21h30", ApesDateHelper.formatSlotHour("2026-10-13T21:30"), "21h30 formatted")
        assertEquals("20h00", ApesDateHelper.formatSlotHour("2026-10-13T20:00"), "20h00 formatted")
        assertEquals("0h15", ApesDateHelper.formatSlotHour("2026-10-14T00:15"), "0h15 formatted")
    }
}
