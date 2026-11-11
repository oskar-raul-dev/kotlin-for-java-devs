package dev.raul.oskar.learning.kotlin.datetime

import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateTimeTest {

    private val ny = ZoneId.of("America/New_York")

    @Test
    fun `strict parsing of US dates`() {
        assertEquals(LocalDate.of(1985, 3, 14), parseUsDate("03/14/1985"))
        assertNull(parseUsDate("02/30/2020"))
        assertNull(parseUsDate("1985-03-14"))
        assertEquals("02/29/2028", formatUsDate(LocalDate.of(2028, 2, 29)))
    }

    @Test
    fun `age uses calendar periods and an injected clock`() {
        val birth = LocalDate.of(1990, 11, 4)
        assertEquals(35, ageAt(birth, LocalDate.of(2026, 11, 3)))
        assertEquals(36, ageAt(birth, LocalDate.of(2026, 11, 4)))
        val clock = Clock.fixed(LocalDate.of(2026, 11, 4).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC)
        assertEquals(36, age(birth, clock))
    }

    @Test
    fun `leap day birthdays turn a year older on March 1st in common years`() {
        val birth = LocalDate.of(1988, 2, 29)
        assertEquals(37, ageAt(birth, LocalDate.of(2026, 2, 28)))
        assertEquals(38, ageAt(birth, LocalDate.of(2026, 3, 1)))
    }

    @Test
    fun `epoch millis depend on the zone`() {
        val ts = 1734739200000          // 2024-12-21T00:00:00Z
        assertEquals(LocalDate.of(2024, 12, 21), dateOf(ts))
        assertEquals(LocalDate.of(2024, 12, 20), dateOf(ts, ny))
        assertEquals("2024-12-21T00:00:00Z", isoInstant(ts))
    }

    @Test
    fun `window of previous days excludes the day itself`() {
        val w = windowBefore(LocalDate.of(2024, 12, 21))
        assertEquals(LocalDate.of(2024, 9, 22), w.start)
        assertEquals(LocalDate.of(2024, 12, 20), w.endInclusive)
        assertTrue(LocalDate.of(2024, 10, 1) in w)
        assertFalse(LocalDate.of(2024, 12, 21) in w)
        assertEquals(90, daysBetween(w.start, w.endInclusive) + 1)
    }

    @Test
    fun `calendar arithmetic`() {
        assertEquals(5, businessDays(LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 9)).size)
        assertEquals(LocalDate.of(2026, 2, 28), plusOneMonth(LocalDate.of(2026, 1, 31)))
        assertEquals(LocalDate.of(2028, 2, 29), lastDayOfMonth(LocalDate.of(2028, 2, 1)))
    }

    @Test
    fun `daylight saving changes the length of a day`() {
        assertEquals(23, hoursInDay(LocalDate.of(2026, 3, 8), ny))
        assertEquals(25, hoursInDay(LocalDate.of(2026, 11, 1), ny))
        assertEquals(24, hoursInDay(LocalDate.of(2026, 3, 8), ZoneId.of("America/Bogota")))
    }

    @Test
    fun `zones durations and times`() {
        assertEquals(
            LocalDateTime.of(2026, 11, 3, 9, 0),
            convert(LocalDateTime.of(2026, 11, 3, 9, 0), ZoneId.of("America/Bogota"), ny),
        )
        assertEquals("1h 02m 05s", formatDuration(Duration.ofSeconds(3725)))
        assertFalse(isOpen(LocalTime.of(8, 59)))
        assertTrue(isOpen(LocalTime.of(9, 0)))
        assertFalse(isOpen(LocalTime.of(18, 0)))
    }
}
